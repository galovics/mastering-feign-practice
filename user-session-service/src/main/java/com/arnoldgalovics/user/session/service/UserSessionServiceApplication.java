package com.arnoldgalovics.user.session.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserSessionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserSessionServiceApplication.class, args);
    }
}
