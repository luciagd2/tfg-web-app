package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio de autenticación que implementa {@link UserDetailsService} para integrar
 * con Spring Security.
 * <p>
 *
 */
@Service
public class ServicioAutenticacion implements UserDetailsService {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    /**
     * Carga los detalles del usuario autenticado por su email.
     *
     * @param email El correo electrónico del usuario (usado como nombre de usuario).
     * @return Un objeto {@link UserDetails} que contiene la información necesaria para la autenticación.
     * @throws UsernameNotFoundException Si no se encuentra un usuario con el email especificado.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = repositorioUsuario.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.emptyList() // Para asignar roles
        );
    }
}
