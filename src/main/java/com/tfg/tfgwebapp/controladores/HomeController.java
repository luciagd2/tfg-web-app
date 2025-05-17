package com.tfg.tfgwebapp.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//Al iniciar la aplicacion nos lleva al login automaticamente
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/login.html";
    }
}