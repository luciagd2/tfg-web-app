package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesDAO.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
