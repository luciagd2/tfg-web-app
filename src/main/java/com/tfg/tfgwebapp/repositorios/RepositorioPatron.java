package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioPatron extends JpaRepository<Patron, Long> {
        List<Patron> findPatronByCreador(Usuario usuario);

        Object findPatronById(Long idPatron);

        List<Patron> findPatronByCreadorAndEstado(Usuario creador, Patron.Estado estado);

        List<Patron> findAllByEstado(Patron.Estado estado);

        //Query por filtros
        @Query("select p from Patron p where p.estado = 'Publicado' and p.dificultad in ?1")
        List<Patron> findAllByDificultadIn(List<Patron.Dificultad> dificultades);

        @Query("SELECT p FROM Patron p LEFT JOIN p.reviews r WHERE p.estado = 'Publicado' GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> findAllOrderByPuntuacionMediaDesc();

        @Query("SELECT p FROM Patron p LEFT JOIN p.reviews r WHERE p.estado = 'Publicado' AND p.dificultad IN :dificultades GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> findAllByDificultadInOrderByPuntuacionMediaDesc(@Param("dificultades") List<Patron.Dificultad> dificultades);

        //Query por busqueda
        @Query("SELECT DISTINCT p FROM Patron p LEFT JOIN p.tags t " +
                "WHERE p.estado = 'Publicado' AND (" +
                "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                "LOWER(p.creador.nombreUsuario) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                "LOWER(t) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
        List<Patron> searchByTituloAutorOTags(@Param("searchTerm") String searchTerm);

        //Query por busqueda y filtros
        @Query("SELECT DISTINCT p FROM Patron p LEFT JOIN p.tags t " +
                "WHERE p.estado = 'Publicado' AND (" +
                "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(p.creador.nombreUsuario) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<Patron> buscarPorTexto(@Param("query") String query);

        @Query("SELECT DISTINCT p FROM Patron p LEFT JOIN p.reviews r LEFT JOIN p.tags t " +
                "WHERE p.estado = 'Publicado' AND p.dificultad IN :dificultades AND (" +
                "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(p.creador.nombreUsuario) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))" +
                "GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> buscarPorTextoYFiltrosOrdenado(@Param("query") String query, @Param("dificultades") List<Patron.Dificultad> dificultades);

        @Query("SELECT DISTINCT p FROM Patron p LEFT JOIN p.tags t " +
                "WHERE p.estado = 'Publicado' AND p.dificultad IN :dificultades AND (" +
                "LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(p.creador.nombreUsuario) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
                "LOWER(t) LIKE LOWER(CONCAT('%', :query, '%')))")
        List<Patron> buscarPorTextoYFiltros(@Param("query") String query, @Param("dificultades") List<Patron.Dificultad> dificultades);

        @Query("SELECT p FROM Patron p WHERE p.estado = 'Publicado' ORDER BY p.id DESC")
        List<Patron> findTop20ByEstadoPublicadoOrderByIdDesc(Pageable pageable);
}
