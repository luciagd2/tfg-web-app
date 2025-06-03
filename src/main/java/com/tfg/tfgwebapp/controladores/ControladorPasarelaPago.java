package com.tfg.tfgwebapp.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Controlador encargado de simular una pasarela de pago para la compra de patrones.
 *
 * <p>Este controlador muestra una vista de pago ficticia y procesa los resultados del mismo.
 * Se utiliza principalmente con fines de prueba o simulación de flujo de pago.
 */
@Controller
@RequestMapping ("api/pasarelaPago")
public class ControladorPasarelaPago {

    /**
     * Muestra la vista de simulación de la pasarela de pago.
     *
     * @param patronId ID del patrón que se desea comprar.
     * @return Vista (HTML) con el formulario de simulación de pago, incluyendo el ID del pedido (patrón).
     */
    @GetMapping("/pedido")
    public ModelAndView vistaPasarelaPago(
            @RequestParam Long patronId
    ) {
        ModelAndView model = new ModelAndView("simulacionPasarelaPago");
        model.addObject("orderId", patronId);
        return model;
    }

    /**
     * Procesa el resultado simulado del pago y redirige al cliente con el estado del pago.
     *
     * @param patronId ID del patrón que fue parte del "pedido".
     * @param result Resultado del pago simulado, por ejemplo "success" o "failure".
     * @return Redirección hacia la página de información del patrón, incluyendo el estado del pago en la URL.
     */
    @PostMapping("/procesar")
    public RedirectView procesarPago(@RequestParam Long patronId,
                                     @RequestParam String result) {
        // Lógica del pago
        return new RedirectView("/infoPatron.html?status=" + result);
    }
}
