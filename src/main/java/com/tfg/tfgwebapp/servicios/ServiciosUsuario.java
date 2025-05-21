package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesDAO.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiciosUsuario {
    @Autowired
    private RepositorioUsuario repositorioUsuario;


    public Optional<Usuario> login(String email, String password) {
        System.out.println("En login");
        return repositorioUsuario.findByEmail(email)
                .filter(u -> u.getPassword().equals(password)); // TODO: sin cifrar
    }

    public boolean registrar(Usuario usuario) {
        if (repositorioUsuario.findByEmail(usuario.getEmail()).isPresent()) {
            return false; // Usuario ya existe
        }
        repositorioUsuario.save(usuario);
        return true;
    }
}
