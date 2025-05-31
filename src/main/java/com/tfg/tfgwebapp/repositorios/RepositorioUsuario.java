package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.patronesComprados pc WHERE pc.id = :patronId")
    boolean existeUsuarioQueHayaCompradoPatron(@Param("patronId") Long patronId);

    @Query("SELECT COUNT(u) > 0 FROM Usuario u JOIN u.patronesEmpezados pe WHERE pe.id = :patronId")
    boolean existeUsuarioQueHayaEmpezadoPatron(@Param("patronId") Long patronId);

    Usuario getUsuarioById(Long id);

    List<Usuario> findByPatronesGuardados_Id(Long idPatron);

    List<Usuario> findByPatronesComprados_Id(Long idPatron);

    Long id(Long id);
}
