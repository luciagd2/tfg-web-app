package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para acceder y gestionar entidades de tipo {@code Notificacion}.
 * Permite consultas personalizadas para obtener notificaciones por usuario y estado de lectura.
 */
public interface RepositorioNotificacion extends JpaRepository<Notificacion, Long> {

    /**
     * Busca una notificación por su ID específico.
     *
     * @param idNotificacion ID de la notificación.
     * @return {@code Optional} con la notificación si existe; vacío si no.
     */
    Optional<Notificacion> findByIdNotificacion(Long idNotificacion);

    /**
     * Obtiene todas las notificaciones de un usuario según si han sido leídas o no.
     *
     * @param usuarioId ID del usuario.
     * @param leido Estado de lectura (true para leídas, false para no leídas).
     * @return Lista de notificaciones filtradas por usuario y estado de lectura.
     */
    List<Notificacion> findByUsuarioIdAndLeido(Long usuarioId, boolean leido);
}
