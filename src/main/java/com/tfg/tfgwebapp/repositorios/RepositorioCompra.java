package com.tfg.tfgwebapp.repositorios;

import com.tfg.tfgwebapp.clasesModelo.Compra;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repositorio para gestionar las entidades {@code Compra}.
 * Proporciona métodos personalizados para verificar relaciones entre usuarios y patrones.
 */
public interface RepositorioCompra extends JpaRepository<Compra, Long>  {
    /**
     * Verifica si existe alguna compra registrada para un patrón dado.
     *
     * @param patron Patrón a verificar.
     * @return {@code true} si hay al menos una compra de ese patrón; {@code false} si no.
     */
    @Query("select (count(c) > 0) from Compra c where c.patron = ?1")
    boolean existsByPatron(Patron patron);
}
