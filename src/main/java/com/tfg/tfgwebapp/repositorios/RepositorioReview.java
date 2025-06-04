package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositorio que gestiona las operaciones sobre la entidad {@code Review}.
 * Permite recuperar reseñas (valoraciones) asociadas a un patrón específico.
 */
public interface RepositorioReview extends JpaRepository<Review, Long> {
        /**
         * Obtiene todas las reseñas asociadas a un patrón específico.
         *
         * @param id ID del patrón.
         * @return Lista de reseñas vinculadas a ese patrón.
         */
        List<Review> findReviewsByPatronId(Long id);
}
