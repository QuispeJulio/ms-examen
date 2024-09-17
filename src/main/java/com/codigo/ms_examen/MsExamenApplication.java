package com.codigo.ms_examen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsExamenApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsExamenApplication.class, args);
    }

}
