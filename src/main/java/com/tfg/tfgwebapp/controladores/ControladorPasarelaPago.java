package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.repositorios.RepositorioCompra;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping ("api/pasarelaPago")
public class ControladorPasarelaPago {

    @Autowired
    private RepositorioPatron repositorioPatron;

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioCompra repositorioCompra;

    @GetMapping("/pedido")
    public ModelAndView vistaPasarelaPago(
            @RequestParam Long patronId
    ) {
        ModelAndView model = new ModelAndView("simulacionPasarelaPago");
        model.addObject("orderId", patronId);
        return model;
    }

    @PostMapping("/procesar")
    public RedirectView procesarPago(@RequestParam Long patronId,
                                     @RequestParam String result) {
        // Lógica del pago
        return new RedirectView("/infoPatron.html?status=" + result);
    }
}
