package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio que proporciona métodos para acceder y manipular entidades de tipo {@code Usuario}
 * en la base de datos. Extiende {@link JpaRepository} para obtener funcionalidades CRUD.
 */
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email Correo electrónico del usuario.
     * @return Un {@code Optional} que contiene el usuario si existe; vacío si no.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe al menos un usuario que haya comprado un patrón específico.
     *
     * @param patronId ID del patrón.
     * @return {@code true} si existe al menos un usuario que compró el patrón; de lo contrario {@code false}.
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.patronesComprados pc WHERE pc.id = :patronId")
    boolean existeUsuarioQueHayaCompradoPatron(@Param("patronId") Long patronId);

    /**
     * Verifica si existe al menos un usuario que haya empezado un patrón específico.
     *
     * @param patronId ID del patrón.
     * @return {@code true} si existe al menos un usuario que empezó el patrón; de lo contrario {@code false}.
     */
    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.patronesEmpezados pe WHERE pe.id = :patronId")
    boolean existeUsuarioQueHayaEmpezadoPatron(@Param("patronId") Long patronId);

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return El usuario correspondiente al ID.
     */
    Usuario getUsuarioById(Long id);

    /**
     * Busca todos los usuarios que tienen guardado un patrón específico.
     *
     * @param idPatron ID del patrón.
     * @return Lista de usuarios que han guardado el patrón.
     */
    List<Usuario> findByPatronesGuardados_Id(Long idPatron);

    /**
     * Busca todos los usuarios que tienen comprado un patrón específico.
     *
     * @param idPatron ID del patrón.
     * @return Lista de usuarios que han guardado el patrón.
     */
    List<Usuario> findByPatronesComprados_Id(Long idPatron);
}
