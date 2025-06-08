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
         * Obtiene patrones filtrados por dificultad y por un query con palabras clave.
         *
         * @param query String de palabras clave.
         * @param dificultades Lista de dificultades.
         * @return Lista de patrones publicados que coinciden con las dificultades dadas.
         */
        @Query(value = """
            SELECT DISTINCT p.*
            FROM patron p
            LEFT JOIN usuario u ON p.id_creador = u.id
            LEFT JOIN patron_tags pt ON p.id = pt.patron_id
            LEFT JOIN review r ON p.id = r.id_patron
            WHERE p.estado = 'Publicado'
              AND p.dificultad IN (:dificultades)
              AND (
                LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.nombre_usuario) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(pt.tag) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            GROUP BY p.id
            ORDER BY AVG(r.puntuacion) DESC
            """, nativeQuery = true)
        List<Patron> findAllFiltradosByPuntuacionMediaDesc( @Param("dificultades") List<String> dificultades, @Param("query") String query);

        /**
         * Obtiene patrones filtrados por dificultad, un query con palabras clave y ordenados por puntuación promedio descendente.
         *
         * @param query String de palabras clave.
         * @param dificultades Lista de dificultades.
         * @return Lista de patrones publicados que coinciden con las dificultades dadas.
         */
        @Query(value = """
            SELECT DISTINCT p.* FROM patron p
            LEFT JOIN usuario u ON p.id_creador = u.id
            LEFT JOIN patron_tags pt ON p.id = pt.patron_id
            WHERE p.estado = 'Publicado'
              AND p.dificultad IN (:dificultades)
              AND (
                LOWER(p.titulo) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.nombre_usuario) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(pt.tag) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            """, nativeQuery = true)
        List<Patron> findAllFiltrados( @Param("dificultades") List<String> dificultades, @Param("query") String query);

        /**
         * Obtiene los últimos 20 patrones publicados, ordenados por ID descendente.
         *
         * @param pageable Objeto {@link Pageable} para limitar resultados.
         * @return Lista de hasta 20 patrones más recientes en estado 'Publicado'.
         */
        @Query("SELECT p FROM Patron p WHERE p.estado = 'Publicado' ORDER BY p.id DESC")
        List<Patron> findTop20ByEstadoPublicadoOrderByIdDesc(Pageable pageable);
}
