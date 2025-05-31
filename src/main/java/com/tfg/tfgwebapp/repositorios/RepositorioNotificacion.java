package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepositorioNotificacion extends JpaRepository<Notificacion, Long> {
    Optional<Notificacion> findByIdNotificacion(Long idNotificacion);

    List<Notificacion> findByUsuarioIdAndLeido(Long usuarioId, boolean leido);
}
