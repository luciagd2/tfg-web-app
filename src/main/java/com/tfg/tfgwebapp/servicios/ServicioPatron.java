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

@Service
public class ServicioPatron {
    @Autowired
    private RepositorioPatron repositorioPatron;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioCompra repositorioCompra;

    public ServicioPatron(RepositorioCompra repositorioCompra, RepositorioUsuario repositorioUsuario) {
        this.repositorioCompra = repositorioCompra;
        this.repositorioUsuario = repositorioUsuario;
    }

    @GetMapping("/patrones/patrones-tienda")
    public List<Patron> obtenerPatrones(Usuario usuario) {
        System.out.println("En servicio obtenerPatrones con usuario " + usuario+ "con id: " + usuario.getId());
        System.out.println("Usuario encontrado (servicio obtenerPatrones)");
        return repositorioPatron.findPatronByCreador(usuario);
    }

    public boolean patronTieneUsuarios(Long patronId) {
        return repositorioUsuario.existeUsuarioQueHayaCompradoPatron(patronId) ||
                repositorioUsuario.existeUsuarioQueHayaEmpezadoPatron(patronId);
    }

    public void cambiarEstadoPatron(Long patronId, Patron.Estado nuevoEstado) {
        Patron patron = repositorioPatron.findById(patronId)
                .orElseThrow(() -> new EntityNotFoundException("Patron no encontrado"));
        patron.setEstado(nuevoEstado);
        repositorioPatron.save(patron);
    }

    public void eliminarPatron(Long patronId) {
        // Si quieres borrar directamente o hacer algo más
        repositorioPatron.deleteById(patronId);
    }
}
