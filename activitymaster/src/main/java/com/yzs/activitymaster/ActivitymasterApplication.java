package com.yzs.activitymaster;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

//开启断路器，实现服务容错保护
@EnableCircuitBreaker
//让服务使用eureka服务器 实现服务注册和发现
@EnableDiscoveryClient
@EnableEurekaClient
@ComponentScan({"com.yzs.activitymaster.*"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ActivitymasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivitymasterApplication.class, args);
    }

}
