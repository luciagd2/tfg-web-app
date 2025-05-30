package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositorioNotificacion extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByUsuarioId(Long idUsuario);
    
}
