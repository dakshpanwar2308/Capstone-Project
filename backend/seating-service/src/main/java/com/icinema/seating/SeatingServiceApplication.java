package com.icinema.seating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.icinema")
public class SeatingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SeatingServiceApplication.class, args);
    }
}
