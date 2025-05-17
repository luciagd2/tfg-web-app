package com.tfg.tfgwebapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class prueba {

    @GetMapping("/")
    public String hello() {
        return "¡Hola mundo desde Spring Boot!";
    }
}
