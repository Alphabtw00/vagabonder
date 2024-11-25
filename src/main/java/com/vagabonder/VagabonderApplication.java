package com.vagabonder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class VagabonderApplication { //todo remove illegalArgument from everywhere

    public static void main(String[] args) {
        SpringApplication.run(VagabonderApplication.class, args);
    }

}
