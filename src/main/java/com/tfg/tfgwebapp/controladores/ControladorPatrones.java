package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesDAO.Patron;
import com.tfg.tfgwebapp.clasesDAO.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.servicios.ServicioPatron;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/patrones")
public class ControladorPatrones {
    private static final Logger logger = LoggerFactory.getLogger(ControladorPatrones.class);

    private final RepositorioUsuario repositorioUsuario;
    private final ServicioPatron servicioPatron;
    private final RepositorioPatron repositorioPatron;

    // Inyección mediante constructor (recomendado)
    @Autowired
    public ControladorPatrones(RepositorioUsuario repositorioUsuario, ServicioPatron servicioPatron, RepositorioPatron repositorioPatron) {
        this.repositorioUsuario = repositorioUsuario;
        this.servicioPatron = servicioPatron;
        this.repositorioPatron = repositorioPatron;
    }

    @GetMapping("/patrones-tienda")
    //public ResponseEntity<List<Patron>> obtenerPatronesUsuario(HttpSession session) {
    /*public ResponseEntity<List<Patron>> obtenerPatronesUsuario() throws IOException {
        logger.info("En controlador obtenerPatronesUsuario");

        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Usuario autenticado: " + usuarioAuth);

        // Comprobar si hay usuario autenticado válido
        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            // Devuelve 401 sin body o con lista vacía para no cambiar el tipo de retorno
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        Object principal = usuarioAuth.getPrincipal();

        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            email = (String) principal;
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        Usuario usuario = usuarioOpt.get();

        // Aquí llamas a tu servicio para obtener patrones del usuario
        List<Patron> patrones = servicioPatron.obtenerPatrones(usuario);

        if (patrones == null) {
            patrones = Collections.emptyList();
        }

        return ResponseEntity.ok(patrones);
    }*/
    public ResponseEntity<List<Patron>> obtenerPatronesUsuario() {
        System.out.println("En controlador obtenerPatronesUsuario");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("Usuario encontrado\n");
        Usuario usuario = usuarioOpt.get();

        //System.out.println("Patrones 1: "+servicioPatron.obtenerPatrones(usuario)+"\n");
        //List<Patron> patrones = servicioPatron.obtenerPatrones(usuario);
        List<Patron> patrones = new ArrayList<>();
        try {
            patrones = repositorioPatron.findPatronByCreador(usuario);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Pecepcion al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
