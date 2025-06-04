package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servicio encargado de la lógica relacionada con los usuarios del sistema.
 * Se comunica con el repositorio de usuarios para realizar operaciones como login y registro.
 */
@Service
public class ServiciosUsuario {

    // Inyección del repositorio de usuarios para acceder a la base de datos.
    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Verifica si un usuario con el email y contraseña especificados existe en la base de datos.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @return Optional con el usuario si las credenciales son válidas; vacío en caso contrario.
     */
    public Optional<Usuario> login(String email, String password) {
        return repositorioUsuario.findByEmail(email)
                .filter(u -> u.getPassword().equals(password)); // TODO: sin cifrar
    }

    /**
     * Registra un nuevo usuario si no existe ya uno con el mismo email.
     *
     * @param usuario Objeto Usuario a registrar.
     * @return true si el usuario fue registrado exitosamente; false si ya existía un usuario con el mismo email.
     */
    public boolean registrar(Usuario usuario) {
        if (repositorioUsuario.findByEmail(usuario.getEmail()).isPresent()) {
            return false; // Usuario ya existe
        }
        repositorioUsuario.save(usuario);
        return true;
    }
}
