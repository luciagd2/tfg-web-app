package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesDAO.Patron;
import com.tfg.tfgwebapp.clasesDAO.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioPatron extends JpaRepository<Patron, Long> {
        List<Patron> findPatronByCreador(Usuario usuario);

        @Query("SELECT p FROM Patron p WHERE p.creador.id = :id")
        List<Patron> findByCreadorId(@Param("id") Long id);

}
