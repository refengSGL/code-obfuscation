package com.mightcell;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: MightCell
 * @description: 启动类
 * @date: Created in 18:27 2023-02-22
 */
@Slf4j
@MapperScan("com.mightcell.mapper")
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
        log.info("Application is running");
    }
}
