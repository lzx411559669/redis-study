package com.example.redisstudy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.redisstudy.mapper")
public class RedisStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisStudyApplication.class, args);
    }

}
