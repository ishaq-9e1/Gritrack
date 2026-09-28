package com.example.gritrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class GritrackApplication {
    public static void main(String[] args) {
        SpringApplication.run(GritrackApplication.class, args);
    }
}
