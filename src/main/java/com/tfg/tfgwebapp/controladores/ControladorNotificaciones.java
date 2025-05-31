package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioNotificacion;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
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
    @Autowired
    private RepositorioPatron repositorioPatron;

    public ControladorNotificaciones(RepositorioNotificacion repositorioNotificacion, RepositorioUsuario repositorioUsuario) {
        this.repositorioNotificacion = repositorioNotificacion;
        this.repositorioUsuario = repositorioUsuario;
    }

    @GetMapping("/obtener-no-leidas")
    public ResponseEntity<?> obtenerNoLeidas() {
        logger.info("Entrando en el método obtenerNoLeidas");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        System.out.println("Usuario: " + usuario);

        List<Notificacion> notifs = repositorioNotificacion.findByUsuarioIdAndLeido(usuario.getId(), false);
        System.out.println("Notifs: " + notifs);
        return ResponseEntity.ok(notifs);
    }

    @PostMapping("/nuevoPatron")
    public ResponseEntity<?> guardarNotificacionNuevoPatron(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionNuevoPatron");
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
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patronEliminado")
    public ResponseEntity<?> guardarNotificacionPatronEliminado(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionNuevoPatron");
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
        List<Usuario> idsUsuariosGuardado = repositorioUsuario.findByPatronesGuardados_Id(idPatron);

        for (Usuario usuario : idsUsuariosGuardado) {
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(usuario);
            notificacion.setTipo(Notificacion.TipoNotificacion.ELIMINADO_GUARDADO);
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nuevaReview")
    public ResponseEntity<?> guardarNotificacionNuevaReview(@RequestParam long idPatron, @RequestParam int puntuacion) {
        logger.info("Entrando en el método guardarNotificacionNuevaReview");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuarioDejaReview = usuarioOpt.get();
        Patron patron = repositorioPatron.findById(idPatron).get();
        Usuario creador = patron.getCreador();

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(creador);
        notificacion.setTipo(Notificacion.TipoNotificacion.CALIFICACION);
        notificacion.setUsuarioRelacionado(usuarioDejaReview);
        notificacion.setPatronRelacionado(patron);
        notificacion.setPuntuacionPatronRelacionado(puntuacion);

        repositorioNotificacion.save(notificacion);

        System.out.println("Notificacion guardada - guardarNotificacionNuevaReview");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patronGuardadoCreador")
    public ResponseEntity<?> guardarNotificacionPatronGuardadoCreador(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoCreador");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuarioQueGuarda = usuarioOpt.get();
        Patron patron = repositorioPatron.findById(idPatron).get();
        Usuario creador = patron.getCreador();

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(creador);
        notificacion.setTipo(Notificacion.TipoNotificacion.GUARDADO_AL_CREADOR);
        notificacion.setUsuarioRelacionado(usuarioQueGuarda);
        notificacion.setPatronRelacionado(patron);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/patronGuardadoUsuario")
    public ResponseEntity<?> guardarNotificacionPatronGuardadoUsuario(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoUsuario");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Patron patron = repositorioPatron.findById(idPatron).get();
        Usuario creador = patron.getCreador();

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(usuario);
        notificacion.setTipo(Notificacion.TipoNotificacion.GUARDADO);
        notificacion.setUsuarioRelacionado(creador);
        notificacion.setPatronRelacionado(patron);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/patronCompraCreador")
    public ResponseEntity<?> guardarNotificacionPatronCompraCreador(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronCompraCreador");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuarioQueGuarda = usuarioOpt.get();
        Patron patron = repositorioPatron.findById(idPatron).get();
        Usuario creador = patron.getCreador();

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(creador);
        notificacion.setTipo(Notificacion.TipoNotificacion.COMPRA_AL_CREADOR);
        notificacion.setUsuarioRelacionado(usuarioQueGuarda);
        notificacion.setPatronRelacionado(patron);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/patronCompraUsuario")
    public ResponseEntity<?> guardarNotificacionPatronCompraUsuario(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronCompraUsuario");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        Patron patron = repositorioPatron.findById(idPatron).get();
        Usuario creador = patron.getCreador();

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(usuario);
        notificacion.setTipo(Notificacion.TipoNotificacion.COMPRA_EXITOSA);
        notificacion.setUsuarioRelacionado(creador);
        notificacion.setPatronRelacionado(patron);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizadoComprados")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoComprados(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoComprados");
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
        List<Usuario> idsUsuariosComprado = repositorioUsuario.findByPatronesComprados_Id(idPatron);

        for (Usuario usuario : idsUsuariosComprado) {
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(usuario);
            notificacion.setTipo(Notificacion.TipoNotificacion.ACTUALIZADO_COMPRADO);
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizadoGuardados")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoGuardados(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoGuardados");
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
        List<Usuario> idsUsuariosGuardado = repositorioUsuario.findByPatronesGuardados_Id(idPatron);

        for (Usuario usuario : idsUsuariosGuardado) {
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(usuario);
            notificacion.setTipo(Notificacion.TipoNotificacion.ACTUALIZADO_GUARDADO);
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizadoPrecio")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoPrecio(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoPrecio");
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
        List<Usuario> idsUsuariosComprado = repositorioUsuario.findByPatronesGuardados_Id(idPatron);

        for (Usuario usuario : idsUsuariosComprado) {
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(usuario);
            notificacion.setTipo(Notificacion.TipoNotificacion.CAMBIO_PRECIO);
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/actualizadoGratis")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoGratis(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoGratis");
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
        List<Usuario> idsUsuariosComprado = repositorioUsuario.findByPatronesGuardados_Id(idPatron);

        for (Usuario usuario : idsUsuariosComprado) {
            Notificacion notificacion = new Notificacion();

            notificacion.setUsuario(usuario);
            notificacion.setTipo(Notificacion.TipoNotificacion.PATRON_GRATIS);
            notificacion.setUsuarioRelacionado(creador);
            notificacion.setPatronRelacionado(repositorioPatron.findById(idPatron).get());

            repositorioNotificacion.save(notificacion);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/seguir")
    public ResponseEntity<?> guardarNotificacionSeguir(@RequestParam long idCreador) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoCreador");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuarioQueSigueA = usuarioOpt.get();
        Usuario creador = repositorioUsuario.getUsuarioById(idCreador);

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(creador);
        notificacion.setTipo(Notificacion.TipoNotificacion.NUEVO_SEGUIDOR);
        notificacion.setUsuarioRelacionado(usuarioQueSigueA);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }




    @PostMapping("/marcar-leido")
    public ResponseEntity<?> marcarLeido(@RequestParam long idNotificacion) {
        Optional<Notificacion> notif = repositorioNotificacion.findByIdNotificacion(idNotificacion);
        notif.get().setLeido(true);
        repositorioNotificacion.save(notif.get());
        return ResponseEntity.ok().build();
    }

}
