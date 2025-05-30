package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Compra;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RepositorioCompra extends JpaRepository<Compra, Long>  {
    boolean existsByUsuarioAndPatron(Usuario usuario, Patron patron);

    @Query("select (count(c) > 0) from Compra c where c.patron = ?1")
    boolean existsByPatron(Patron patron);
}
