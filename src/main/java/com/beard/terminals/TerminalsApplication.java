package com.beard.terminals;

import com.cxytiandi.elasticjob.annotation.EnableElasticJob;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author angry_beard
 */
@SpringBootApplication
@EnableElasticJob
@MapperScan({"com.beard.terminals.mapper"})
public class TerminalsApplication {

    public static void main(String[] args) {
        SpringApplication.run(TerminalsApplication.class, args);
    }

}
