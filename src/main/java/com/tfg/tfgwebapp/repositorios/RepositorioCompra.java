package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Compra;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioCompra extends JpaRepository<Compra, Long>  {
    boolean existsByUsuarioAndPatron(Usuario usuario, Patron patron);
}
