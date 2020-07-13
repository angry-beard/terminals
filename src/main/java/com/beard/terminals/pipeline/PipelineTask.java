package com.beard.terminals.pipeline;

import com.google.common.base.Preconditions;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Builder
@Slf4j
@ToString
public class PipelineTask {

    private final PipelineMapper pipelineMapper;

    @Getter
    private final PipelineProcess startingProcess;

    @Getter
    private final Object requestParam;

    @Getter
    private final String serial;

    @Getter
    private final String name;

    private Object resultParam;

    @Getter
    private Integer currentProcessId;

    @Getter
    private Integer currentProcessOrder;

    @Getter
    private PipelineExecStatus execStatus;

    @SuppressWarnings("unchecked")
    public void run() {
        try {
            Preconditions.checkNotNull(startingProcess, "起始节点 startingProcess 不能够为空");

            PipelineProcess currentProcess = startingProcess;

            boolean runSuccess = pipelineMapper.updateTaskRunning(this);

            if (!runSuccess) {
                return;
            }

            while (!currentProcess.isTerminal()) {
                ProcessParameter processParameter = buildProcessParameterMap(resultParam);

                //TODO:fixme 这里的currentProcessId需要重新拿一下，或者更新的时候判断下当前process的状态(需要重新读数据库状态),如果不是PAUSED或者INIT,就要抛错
                pipelineMapper.updateProcessRunning(currentProcessId);

                ProcessResult processResult = currentProcess.process(processParameter);
                log.debug("流水线 {}-{} 结束处理 {}，processResult={}", name, serial, String.valueOf(currentProcess.getName()), processResult.toString());

                Preconditions.checkNotNull(processResult);

                if (isProcessPaused(processResult)) {
                    pipelineMapper.storePausedPipeline(this);
                    log.debug("流水线 {}-{} 暂停处理，processResult={}", name, serial, processResult.toString());
                    break;
                } else {
                    /* 当前 PipelineProcess 完成处理，保存其状态，然后获取下一个 PipelineProcess 进行处理 */
                    PipelineProcess nextProcess = processResult.getNextProcess();

                    int nextProcessOrder = currentProcessOrder + 1;
                    Integer nextProcessId = pipelineMapper.updateProcessDoneAndInitNextProcess(serial, currentProcessId, processResult, nextProcessOrder);

                    currentProcess = nextProcess;
                    currentProcessId = nextProcessId;
                    currentProcessOrder = nextProcessOrder;
                    resultParam = processResult.getResultParam();
                    Preconditions.checkNotNull(currentProcess, "非终止节点 PipelineProcess 返回 nextProcess 为空");
                    log.debug("流水线 {}-{} 执行下一个节点 {}", name, serial, String.valueOf(nextProcess.getName()));
                }
            }

            if (currentProcess.isTerminal()) {
                currentProcess.process(buildProcessParameterMap(resultParam));
                pipelineMapper.updateTaskDone(this);
                pipelineMapper.updateProcessDone(currentProcessId);
                log.debug("流水线 {}-{} 完成处理", name, serial);
            }

        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                log.warn("客户端参数错误, serial = {}", serial, e);
            } else {
                log.error("流水线处理异常: serial = {}", serial, e);
            }
            pipelineMapper.storePausedPipeline(this);
        }
    }

    private ProcessParameter buildProcessParameterMap(Object resultParam) {
        return ProcessParameter.builder()
                .requestParam(requestParam)
                .order(currentProcessOrder)
                .id(currentProcessId)
                .resultParam(resultParam)
                .pipelineTask(this)
                .build();
    }

    private boolean isProcessPaused(ProcessResult processResult) {
        Preconditions.checkNotNull(processResult.getProcessExecStatus());
        return processResult.getProcessExecStatus().equals(PipelineExecStatus.PAUSED);
    }
}
