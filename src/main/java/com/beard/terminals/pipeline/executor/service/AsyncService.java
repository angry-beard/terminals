package com.beard.terminals.pipeline.executor.service;

import com.beard.terminals.pipeline.PipelineExecStatus;
import com.beard.terminals.pipeline.PipelineTaskFactory;
import com.beard.terminals.pipeline.domain.PipelineTask;
import com.beard.terminals.pipeline.mapper.PipelineTaskMapper;
import com.beard.terminals.pipeline.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by angry_beard on 2020/7/14.
 */
@Slf4j
@Service
public class AsyncService {

    private final PipelineTaskMapper pipelineTaskMapper;
    private final PipelineTaskFactory pipelineTaskFactory;

    public AsyncService(PipelineTaskFactory pipelineTaskFactory, PipelineTaskMapper pipelineTaskMapper) {
        this.pipelineTaskFactory = pipelineTaskFactory;
        this.pipelineTaskMapper = pipelineTaskMapper;
    }

    @Async("doPipelineTaskExecutor")
    public void processTask(PipelineTask task) {
        try {
            PipelineTask bankPipelineTask = pipelineTaskMapper.selectByPrimaryKey(task.getId());
            if (PipelineExecStatus.PAUSED.toString().equals(bankPipelineTask.getExecStatus())
                    || PipelineExecStatus.INIT.toString().equals(bankPipelineTask.getExecStatus())) {
                pipelineTaskFactory.restoreTaskFromDatabase(task.getSerial()).run();
            }
        } catch (Exception e) {
            log.error("pipeline执行异常:" + JsonUtils.toJSONString(task), e);
        }
    }
}
