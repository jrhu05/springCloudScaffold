package com.hytcshare.springCloudConfigureClient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConfigureClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigureClientApplication.class, args);
    }
}
