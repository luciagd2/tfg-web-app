package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioPatron extends JpaRepository<Patron, Long> {
        List<Patron> findPatronByCreador(Usuario usuario);

        Object findPatronById(Long idPatron);

        List<Patron> findPatronByCreadorAndPublicado(Usuario usuario, boolean b);

        List<Patron> findAllByPublicado(boolean b);

        @Query("select p from Patron p where p.publicado = true and p.dificultad in ?1")
        List<Patron> findAllByDificultadIn(List<Patron.Dificultad> dificultades);

        @Query("SELECT p FROM Patron p LEFT JOIN p.reviews r WHERE p.publicado = true GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> findAllOrderByPuntuacionMediaDesc();

        @Query("SELECT p FROM Patron p LEFT JOIN p.reviews r WHERE p.publicado = true AND p.dificultad IN :dificultades GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> findAllByDificultadInOrderByPuntuacionMediaDesc(@Param("dificultades") List<Patron.Dificultad> dificultades);

        @Query("""
        SELECT DISTINCT p FROM Patron p
        LEFT JOIN p.reviews r
        LEFT JOIN p.tags tag
        WHERE (:busqueda IS NULL\s
               OR LOWER(p.titulo) LIKE CONCAT('%', LOWER(:busqueda), '%')\s
               OR LOWER(p.creador.nombreUsuario) LIKE CONCAT('%', LOWER(:busqueda), '%')\s
               OR LOWER(tag) LIKE CONCAT('%', LOWER(:busqueda), '%'))
        AND (
             (:principiante = true AND p.dificultad = 'Principiante') OR
             (:intermedio = true AND p.dificultad = 'Intermedio') OR
             (:avanzado = true AND p.dificultad = 'Avanzado')
        )
        AND p.publicado = true
        GROUP BY p
        ORDER BY AVG(r.puntuacion) DESC
        """)
        List<Patron> buscarConFiltrosYTexto(
                @Param("principiante") boolean principiante,
                @Param("intermedio") boolean intermedio,
                @Param("avanzado") boolean avanzado,
                @Param("titulo") String textoBusqueda
        );

        @Query("""
        SELECT p FROM Patron p
        LEFT JOIN p.reviews r
        LEFT JOIN p.tags tag
        WHERE (:busqueda IS NULL\s
              OR LOWER(p.titulo) LIKE CONCAT('%', LOWER(:busqueda), '%')\s
              OR LOWER(p.creador.nombreUsuario) LIKE CONCAT('%', LOWER(:busqueda), '%')\s
              OR LOWER(tag) LIKE CONCAT('%', LOWER(:busqueda), '%'))
        AND (
            (:principiante = true AND p.dificultad = 'Principiante') OR
            (:intermedio = true AND p.dificultad = 'Intermedio') OR
            (:avanzado = true AND p.dificultad = 'Avanzado')
        )
        AND p.publicado = true
        GROUP BY p
        ORDER BY AVG(r.puntuacion) DESC
        """)
        List<Patron> buscarConFiltrosYTextoOrdenadoPorPuntuacion(
                @Param("principiante") boolean principiante,
                @Param("intermedio") boolean intermedio,
                @Param("avanzado") boolean avanzado,
                @Param("busqueda") String busqueda
        );

}
