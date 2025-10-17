package com.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class CoinCommonApplication {
    public static void main(String[] args) {

        org.springframework.boot.SpringApplication.run(CoinCommonApplication.class, args);
    }
}
