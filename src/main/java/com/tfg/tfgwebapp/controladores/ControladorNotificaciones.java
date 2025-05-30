package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioNotificacion;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notificaciones")
public class ControladorNotificaciones {
    //TODO: borrar, es solo para mensajes de log
    private static final Logger logger = LoggerFactory.getLogger(ControladorUsuario.class);

    @Autowired
    private RepositorioNotificacion repositorioNotificacion;
    private RepositorioUsuario repositorioUsuario;

    public ControladorNotificaciones(RepositorioNotificacion repositorioNotificacion, RepositorioUsuario repositorioUsuario) {
        this.repositorioNotificacion = repositorioNotificacion;
        this.repositorioUsuario = repositorioUsuario;
    }

    @GetMapping("/{idUsuario}")
    public List<Notificacion> obtenerNotificaciones(@PathVariable Long idUsuario) {
        return repositorioNotificacion.findByUsuarioId(idUsuario);
    }

    @PostMapping("/nuevoPatron")
    public ResponseEntity<?> guardarNotificacionNuevoPatron(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacion");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario creador = usuarioOpt.get();
        List<Long> idsSeguidores = creador.getIdsSeguidores();

        for (Long idSeguidor : idsSeguidores) {
            Usuario seguidor = repositorioUsuario.findById(idSeguidor).get();
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(seguidor);
            notificacion.setTipo(Notificacion.TipoNotificacion.NUEVO_PATRON_CREADOR);
            notificacion.setIdUsuarioRelacionado(creador.getId());
            notificacion.setIdPatronRelacionado(idPatron);

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }


}
