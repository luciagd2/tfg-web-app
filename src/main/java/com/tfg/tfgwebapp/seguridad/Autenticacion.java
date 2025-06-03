package com.tfg.tfgwebapp.seguridad;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Componente Spring que maneja la lógica de autenticación de usuarios.
 * <p>
 * Esta clase se encarga de validar si el usuario actual está autenticado
 * y, en caso afirmativo, obtiene los datos del usuario desde el repositorio
 * de usuarios para ser retornados como respuesta.
 * <p>
 * Depende del componente RepositorioUsuario para acceder a los datos de los usuarios.
 */
@Component
public class Autenticacion {
    private final RepositorioUsuario repositorioUsuario;

    /**
     * Constructor que inyecta el repositorio de usuarios.
     *
     * @param repositorioUsuario Repositorio para acceder a los datos de los usuarios.
     */
    @Autowired
    public Autenticacion(RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    /**
     * Verifica si el usuario actual está autenticado y obtiene su información.
     *
     * @return ResponseEntity<?> una respuesta HTTP:
     * <ul>
     *     <li>200 OK con el objeto Usuario si la autenticación es válida.</li>
     *     <li>401 UNAUTHORIZED con un mensaje si el usuario no está autenticado o no se encuentra en la base de datos.</li>
     * </ul>
     */
    public ResponseEntity<?> autenticar() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        return ResponseEntity.ok(usuarioOpt.get());
    }
}
