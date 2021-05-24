package ru.lab.hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class LabHunterApplication {

    public static void main(String[] args) {
        SpringApplication.run(LabHunterApplication.class, args);
    }
}
