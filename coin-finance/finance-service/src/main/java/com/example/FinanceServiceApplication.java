package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.mapper")
@EnableFeignClients(basePackages = "com.example.feign")
public class FinanceServiceApplication {

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(FinanceServiceApplication.class, args);
    }
}
