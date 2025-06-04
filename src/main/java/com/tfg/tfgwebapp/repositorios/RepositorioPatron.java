package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio que permite realizar operaciones de consulta sobre la entidad {@code Patron}.
 * Utiliza Spring Data JPA para consultas dinámicas y personalizadas.
 */
public interface RepositorioPatron extends JpaRepository<Patron, Long> {

        /**
         * Obtiene todos los patrones creados por un usuario específico.
         *
         * @param usuario Usuario creador.
         * @return Lista de patrones asociados al usuario.
         */
        List<Patron> findPatronByCreador(Usuario usuario);

        /**
         * Busca un patrón por su ID.
         *
         * @param idPatron ID del patrón.
         * @return Objeto patrón. *Nota:* el retorno como Object no es recomendable; lo ideal es {@code Optional<Patron>} o {@code Patron}.
         */
        Object findPatronById(Long idPatron);

        /**
         * Obtiene patrones creados por un usuario con un estado específico.
         *
         * @param creador Usuario creador.
         * @param estado Estado del patrón.
         * @return Lista de patrones filtrados por creador y estado.
         */
        List<Patron> findPatronByCreadorAndEstado(Usuario creador, Patron.Estado estado);

        /**
         * Obtiene todos los patrones con un estado específico.
         *
         * @param estado Estado del patrón.
         * @return Lista de patrones con dicho estado.
         */
        List<Patron> findAllByEstado(Patron.Estado estado);

        /**
         * Obtiene patrones con estado "Publicado" y cuyas dificultades estén en la lista dada.
         *
         * @param dificultades Lista de dificultades a filtrar.
         * @return Lista de patrones filtrados por dificultad.
         */
        @Query("select p from Patron p where p.estado = 'Publicado' and p.dificultad in ?1")
        List<Patron> findAllByDificultadIn(List<Patron.Dificultad> dificultades);

        /**
         * Obtiene todos los patrones publicados ordenados por puntuación promedio de mayor a menor.
         *
         * @return Lista de patrones ordenada por puntuación media descendente.
         */
        @Query("SELECT p FROM Patron p LEFT JOIN p.reviews r WHERE p.estado = 'Publicado' GROUP BY p ORDER BY AVG(r.puntuacion) DESC")
        List<Patron> findAllOrderByPuntuacionMediaDesc();

        /**
         * Obtiene patrones filtrados por dificultad y ordenados por puntuación promedio descendente.
         *
         * @param dificultades Lista de dificultades.
         * @return Lista de patrones publicados que coinciden con las dificultades dadas.
         */
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

        /**
         * Obtiene los últimos 20 patrones publicados, ordenados por ID descendente.
         *
         * @param pageable Objeto {@link Pageable} para limitar resultados.
         * @return Lista de hasta 20 patrones más recientes en estado 'Publicado'.
         */
        @Query("SELECT p FROM Patron p WHERE p.estado = 'Publicado' ORDER BY p.id DESC")
        List<Patron> findTop20ByEstadoPublicadoOrderByIdDesc(Pageable pageable);
}
