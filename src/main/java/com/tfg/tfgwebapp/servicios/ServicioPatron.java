package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
public class ServicioPatron {
    @Autowired
    private RepositorioPatron repositorioPatron;
    private RepositorioUsuario repositorioUsuario;

    @GetMapping("/patrones/patrones-tienda")
    /*
    public List<Patron> obtenerPatronesDelUsuario(String username) {
        Usuario usuario = repositorioUsuario.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return repositorioPatron.findByIdCreador(usuario.getId());
    }
    */
    public List<Patron> obtenerPatrones(Usuario usuario) {
        System.out.println("En servicio obtenerPatrones con usuario " + usuario+ "con id: " + usuario.getId());
        //List<Patron> patrones = repositorioPatron.findByIdCreador(usuario.getId());
        //return ResponseEntity.ok(patrones);
        /*
        if (usuario == null) {
            System.out.println("Usuario no encontrado (servicio obtenerPatrones)");
            return Collections.emptyList();
        }*/
        System.out.println("Usuario encontrado (servicio obtenerPatrones)");
        return repositorioPatron.findPatronByCreador(usuario);
    }

}
