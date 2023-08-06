package com.ksyun.start.camp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 微服务注册中心
 */
@SpringBootApplication
public class RegistryApp {
    public static void main(String[] args) {
        SpringApplication.run(RegistryApp.class, args);
    }
}
