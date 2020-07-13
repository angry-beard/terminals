package com.beard.terminals;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author angry_beard
 */
@SpringBootApplication
@MapperScan({"com.beard.terminals.dao"})
public class TerminalsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerminalsApplication.class, args);
	}

}
