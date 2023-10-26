package com.example.OrderAppTest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.example.OrderApp", "com.example.OrderAppTest"})
public class TestConfig {
    // Any additional config
}