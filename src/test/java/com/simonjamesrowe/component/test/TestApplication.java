package com.simonjamesrowe.component.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestApplication {

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(TestApplication.class);
        app.run(args);
    }
}
