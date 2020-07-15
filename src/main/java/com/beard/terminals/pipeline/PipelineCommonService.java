package com.beard.terminals.pipeline;

import com.beard.terminals.pipeline.domain.PipelineTask;
import com.beard.terminals.pipeline.domain.PipelineTaskExample;
import com.beard.terminals.pipeline.executor.service.AsyncService;
import com.beard.terminals.pipeline.mapper.PipelineTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class PipelineCommonService {


    private final PipelineTaskMapper pipelineTaskMapper;
    private final AsyncService asyncService;

    public PipelineCommonService(PipelineTaskMapper pipelineTaskMapper, AsyncService asyncService) {
        this.pipelineTaskMapper = pipelineTaskMapper;
        this.asyncService = asyncService;
    }

    public void retryTask(String taskName) {
        PipelineTaskExample example = new PipelineTaskExample();
        example.createCriteria().andExecStatusEqualTo(PipelineExecStatus.INIT.toString()).andNameEqualTo(taskName)
                .andCreatedAtLessThan(new DateTime().minusMinutes(2).toDate());
        example.setOrderByClause("id limit 10");
        List<PipelineTask> initTaskList = pipelineTaskMapper.selectByExampleWithBLOBs(example);
        if (!CollectionUtils.isEmpty(initTaskList)) {
            ConcurrentLinkedQueue<PipelineTask> queue = new ConcurrentLinkedQueue<>(initTaskList);
            PipelineTask task;
            while ((task = queue.poll()) != null) {
                //TODO 分布式锁
                asyncService.processTask(task);
            }

        }
    }
}