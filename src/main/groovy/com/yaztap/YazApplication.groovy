package com.yaztap

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class YazApplication {

    static void main(String[] args) {
        SpringApplication app = new SpringApplication(YazApplication.class)
        app.setDefaultProperties(Collections
                .singletonMap("server.port", "8089"))
        try {
            app.run(args)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

}