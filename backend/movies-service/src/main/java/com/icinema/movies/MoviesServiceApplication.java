package com.icinema.movies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.icinema")
public class MoviesServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoviesServiceApplication.class, args);
    }
}
