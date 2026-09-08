package com.empresa.maestra_dyd_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MaestraDydBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MaestraDydBootApplication.class, args);
    }

}