package com.yiqipin.backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yiqipin.backend.mapper")
public class YiqipinBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YiqipinBackendApplication.class, args);
    }
}
