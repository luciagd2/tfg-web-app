package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesDAO.Patron;
import com.tfg.tfgwebapp.clasesDAO.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepositorioPatron extends JpaRepository<Patron, Long> {
        List<Patron> findPatronByCreador(Usuario usuario);

        Object findPatronById(Long idPatron);

        List<Patron> findPatronByCreadorAndPublicado(Usuario usuario, boolean b);

        List<Patron> findAllByPublicado(boolean b);

        List<Patron> findAllByDificultad(String dificultad);

        List<Patron> findPatronByPrecio(Double precio);
}
