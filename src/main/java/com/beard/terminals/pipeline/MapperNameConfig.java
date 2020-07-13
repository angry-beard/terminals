package com.beard.terminals.pipeline;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by angry_beard on 2020/7/13.
 */
@Configuration
@MapperScan({"com.beard.terminals.pipeline.mapper"})
public class MapperNameConfig {

    @Bean(name = "taskMapperClassName")
    public String getTaskMapperClassName() {
        return "com.beard.terminals.pipeline.mapper.PipelineTaskMapper";
    }

    @Bean(name = "processMapperClassName")
    public String getProcessMapperClassName() {
        return "com.beard.terminals.pipeline.mapper.PipelineProcessMapper";
    }
}
