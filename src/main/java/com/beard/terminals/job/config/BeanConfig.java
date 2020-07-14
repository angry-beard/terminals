package com.beard.terminals.job.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Created by angry_beard on 2020/7/14.
 */
@Configuration
public class BeanConfig {
    /**
     * 任务执行事件数据源
     *
     * @return
     */
    @Bean("datasource")
    @ConfigurationProperties("spring.datasource.druid")
    public DataSource dataSourceTwo() {
        return DruidDataSourceBuilder.create().build();
    }
}
