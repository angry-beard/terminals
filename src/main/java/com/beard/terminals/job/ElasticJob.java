package com.beard.terminals.job;

import com.beard.terminals.constant.PipelineTaskName;
import com.beard.terminals.pipeline.PipelineCommonService;
import com.beard.terminals.pipeline.utils.JsonUtils;
import com.cxytiandi.elasticjob.annotation.ElasticJobConf;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by angry_beard on 2020/7/14.
 */
@Slf4j
@ElasticJobConf(name = "ElasticJob", cron = "0/10 * * * * ?", shardingItemParameters = "0=A,1=B", description = "简单任务", eventTraceRdbDataSource = "datasource")
public class ElasticJob implements SimpleJob {

    private final PipelineCommonService pipelineCommonService;

    public ElasticJob(PipelineCommonService pipelineCommonService) {
        this.pipelineCommonService = pipelineCommonService;
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("threadName:{};context:{}", Thread.currentThread().getName(), JsonUtils.toJSONString(shardingContext));
        pipelineCommonService.retryTask(PipelineTaskName.TEST_PIPELINE.toString());
    }
}
