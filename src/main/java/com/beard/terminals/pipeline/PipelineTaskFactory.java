package com.beard.terminals.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Created by angry_beard on 2020/7/13.
 * <p>
 * 使用数据库进行状态持久化的流水线任务工厂
 * 需要在 Spring 容器中运行
 * </p>
 */
@Slf4j
@Component
public class PipelineTaskFactory {

    private final PipelineMapper pipelineMapper;

    public PipelineTaskFactory(PipelineMapper pipelineMapper) {
        Preconditions.checkNotNull(pipelineMapper);
        this.pipelineMapper = pipelineMapper;
    }

    /**
     * 如果数据库中有对应 serial 的 task，则调用 restore 方法，否则新建一个 task
     *
     * @param serial       流水线唯一流水号
     * @param name         流水线任务名称
     * @param headProcess  流水线最先需要执行的任务
     * @param requestParam 流水线执行的请求参数
     * @return 一个新的 PipelineTask
     */
    public PipelineTask restoreOrNewPipelineTask(String serial, String name, PipelineProcess headProcess, Object requestParam) {
        Object taskDomain = pipelineMapper.selectPausedTaskOrNull(serial);
        if (taskDomain == null) {
            return newPipelineTask(serial, name, headProcess, requestParam);
        } else {
            return restoreTaskFromDatabase(serial);
        }
    }

    /**
     * 创建一个新的 PipelineTask，同时将新任务的状态保存进数据库
     *
     * @param serial       流水线唯一流水号
     * @param name         流水线任务名称
     * @param headProcess  流水线最先需要执行的任务
     * @param requestParam 流水线执行的请求参数
     * @return 一个新的 PipelineTask
     */
    public PipelineTask newPipelineTask(String serial, String name, PipelineProcess headProcess, Object requestParam) {
        try {
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(serial);
            Preconditions.checkNotNull(headProcess);
            Preconditions.checkNotNull(requestParam);
            Integer processId = pipelineMapper.insertTaskAndStartingProcess(serial, name, headProcess, requestParam);
            log.debug("创建新流水线 {}，流水号 {}，第一个 process 为 {}，参数 {}", String.valueOf(name), serial, String.valueOf(headProcess.getName()), new ObjectMapper().writeValueAsString(requestParam));
            return PipelineTask.builder()
                    .pipelineMapper(pipelineMapper)
                    .serial(serial)
                    .name(name)
                    .requestParam(requestParam)
                    .currentProcessId(processId)
                    .currentProcessOrder(PipelineMapper.STARTING_PROCESS_ORDER)
                    .startingProcess(headProcess)
                    .execStatus(PipelineExecStatus.INIT)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从数据库中重建一个 pipeline 任务。
     *
     * @param serial 流水线唯一标识流水号
     * @return PipelineTask
     */
    public PipelineTask restoreTaskFromDatabase(String serial) {
        return pipelineMapper.restoreTask(serial);
    }
}