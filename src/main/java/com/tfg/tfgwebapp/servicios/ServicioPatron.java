package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioCompra;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Servicio encargado de la gestión de los patrones creados por usuarios.
 * Incluye funciones para obtener patrones, verificar relaciones con usuarios, cambiar estados y eliminarlos.
 */
@Service
public class ServicioPatron {
    @Autowired
    private RepositorioPatron repositorioPatron;
    private RepositorioUsuario repositorioUsuario;
    /**
     * Constructor para inyectar los repositorios necesarios que no pueden ser autowired directamente.
     *
     * @param repositorioUsuario Repositorio para acceder a datos de usuarios.
     * @param repositorioPatron Repositorio para acceder a datos de patrones.
     */
    public ServicioPatron(RepositorioUsuario repositorioUsuario, RepositorioPatron repositorioPatron) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPatron = repositorioPatron;
    }

    /**
     * Obtiene la lista de patrones creados por un usuario específico.
     *
     * @param usuario Usuario cuyo ID se utiliza para buscar patrones creados.
     * @return Lista de patrones asociados al usuario.
     */
    @GetMapping("/patrones/patrones-tienda")
    public List<Patron> obtenerPatrones(Usuario usuario) {
        System.out.println("En servicio obtenerPatrones con usuario " + usuario+ "con id: " + usuario.getId());
        System.out.println("Usuario encontrado (servicio obtenerPatrones)");
        return repositorioPatron.findPatronByCreador(usuario);
    }

    /**
     * Verifica si existen usuarios que hayan comprado o comenzado un patrón específico.
     *
     * @param patronId ID del patrón a verificar.
     * @return true si hay usuarios asociados; false si no.
     */
    public boolean patronTieneUsuarios(Long patronId) {
        return repositorioUsuario.existeUsuarioQueHayaCompradoPatron(patronId) ||
                repositorioUsuario.existeUsuarioQueHayaEmpezadoPatron(patronId);
    }

    /**
     * Cambia el estado de un patrón (por ejemplo: ACTIVO, INACTIVO, ELIMINADO).
     *
     * @param patronId ID del patrón a modificar.
     * @param nuevoEstado Nuevo estado a establecer.
     * @throws EntityNotFoundException si el patrón no se encuentra en la base de datos.
     */
    public void cambiarEstadoPatron(Long patronId, Patron.Estado nuevoEstado) {
        Patron patron = repositorioPatron.findById(patronId)
                .orElseThrow(() -> new EntityNotFoundException("Patron no encontrado"));
        patron.setEstado(nuevoEstado);
        repositorioPatron.save(patron);
    }

    /**
     * Elimina un patrón de la base de datos usando su ID.
     *
     * @param patronId ID del patrón a eliminar.
     */
    public void eliminarPatron(Long patronId) {
        // Si quieres borrar directamente o hacer algo más
        repositorioPatron.deleteById(patronId);
    }
}
