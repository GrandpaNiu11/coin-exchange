package com.example;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan( value ="com.example")
@MapperScan(value = "com.example.mapper")
public class MatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchApplication.class,args);
    }
}
