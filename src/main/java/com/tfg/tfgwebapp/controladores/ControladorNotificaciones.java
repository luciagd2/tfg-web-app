package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Notificacion;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioNotificacion;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.seguridad.Autenticacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión de notificaciones de los usuarios.
 * <p>
 * Proporciona endpoints para:
 * <ul>
 *     <li>Obtener notificaciones no leídas</li>
 *     <li>Marcar notificaciones como leídas</li>
 *     <li>Crear notificaciones cuando un creador publica un nuevo patrón</li>
 *     <li>Crear notificaciones cuando un creador elimina un patrón</li>
 *     <li>Crear notificaciones cuando un usuario realiza una compra exitosa de un patrón</li>
 *     <li>Crear notificaciones cuando guarda un patron</li>
 *     <li>Crear notificaciones cuando un creador cambia el precio de un patrón</li>
 *     <li>Crear notificaciones cuando un creador actualiza un patrón</li>
 *     <li>Crear notificaciones cuando un usuario califica un patrón</li>
 *     <li>Crear notificaciones cuando un usuario comienza a seguir a un creador</li>
 * </ul>
 *
 * Todos los métodos requieren que el usuario esté autenticado. La autenticación
 * se gestiona mediante el componente {@link Autenticacion}.
 */
@RestController
@RequestMapping("/api/notificaciones")
public class ControladorNotificaciones {
    //TODO: borrar, es solo para mensajes de log
    private static final Logger logger = LoggerFactory.getLogger(ControladorUsuario.class);

    @Autowired
    private RepositorioNotificacion repositorioNotificacion;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioPatron repositorioPatron;
    private Autenticacion autenticacion;

    public ControladorNotificaciones(RepositorioNotificacion repositorioNotificacion, RepositorioUsuario repositorioUsuario, RepositorioPatron repositorioPatron, Autenticacion autenticacion) {
        this.repositorioNotificacion = repositorioNotificacion;
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPatron = repositorioPatron;
        this.autenticacion = autenticacion;
    }

    /**
     * Obtiene todas las notificaciones no leídas del usuario autenticado.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, busca en la base de datos todas las
     * notificaciones asociadas al usuario cuyo campo {@code leido} sea {@code false}.
     *
     * @return {@link ResponseEntity} que contiene una lista de notificaciones no leídas si el usuario está autenticado,
     *         o un estado HTTP 401 si no lo está.
     */
    @GetMapping("/obtener-no-leidas")
    public ResponseEntity<?> obtenerNoLeidas() {
        logger.info("Entrando en el método obtenerNoLeidas");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        List<Notificacion> notifs = repositorioNotificacion.findByUsuarioIdAndLeido(usuario.getId(), false);
        return ResponseEntity.ok(notifs);
    }

    /**
     * Crea y guarda una notificación de tipo {@code NUEVO_PATRON_CREADOR} para todos los seguidores
     * del usuario autenticado cuando este publica un nuevo patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene todos los seguidores del usuario autenticado
     * y crea una notificación para cada uno, asociándola al patrón especificado por el parámetro.
     *
     * @param idPatron ID del nuevo patrón que ha publicado el creador. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK si todas las notificaciones se guardan correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/nuevoPatron")
    public ResponseEntity<?> guardarNotificacionNuevoPatron(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionNuevoPatron");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code ELIMINADO_GUARDADO} para todos los usuarios
     * que hayan guardado el patrón eliminado.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene todos los usuarios que hayan guardado el patrón eliminado
     * y crea una notificación para cada uno, asociándola al patrón especificado por el parámetro.
     *
     * @param idPatron ID del patrón que ha eliminado el creador. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/patronEliminado")
    public ResponseEntity<?> guardarNotificacionPatronEliminado(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionNuevoPatron");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code CALIFICACION} para el creador del patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el usuario creador del patrón en el que ha dejado
     * la reseña y crea una notificación para este, asociándola al patrón especificado y la puntuación
     * por los parámetros.
     *
     * @param idPatron ID del patrón al que se le ha dejado la reseña. Se usa para asociar el patrón con la notificación.
     * @param puntuacion Puntuacion que se le ha dado al patrón en la reseña.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/nuevaReview")
    public ResponseEntity<?> guardarNotificacionNuevaReview(@RequestParam long idPatron, @RequestParam int puntuacion) {
        logger.info("Entrando en el método guardarNotificacionNuevaReview");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuarioDejaReview = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code GUARDADO_AL_CREADOR} para el creador del patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación para este,
     * asociándola al patrón especificado por el parámetro y al usuario autenticado.
     *
     * @param idPatron ID del patrón que ha guardado el usuario autenticado. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/patronGuardadoCreador")
    public ResponseEntity<?> guardarNotificacionPatronGuardadoCreador(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoCreador");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuarioQueGuarda = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code GUARDADO} para el usuario que guarda el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación para el usuario autenticado,
     * asociándola al patrón especificado por el parámetro y al creador.
     *
     * @param idPatron ID del patrón que ha guardado el usuario autenticado. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/patronGuardadoUsuario")
    public ResponseEntity<?> guardarNotificacionPatronGuardadoUsuario(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code COMPRA_AL_CREADOR} para el creador del patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación para este,
     * asociándola al patrón especificado por el parámetro y al usuario autenticado.
     *
     * @param idPatron ID del patrón que ha comprado el usuario autenticado. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK si la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/patronCompraCreador")
    public ResponseEntity<?> guardarNotificacionPatronCompraCreador(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronCompraCreador");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuarioQueGuarda = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code COMPRA_EXITOSA} para el usuario que compra el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación para el usuario autenticado,
     * asociándola al patrón especificado por el parámetro y el creador del patrón.
     *
     * @param idPatron ID del patrón que ha comprado el usuario autenticado. Se usa para asociar el patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/patronCompraUsuario")
    public ResponseEntity<?> guardarNotificacionPatronCompraUsuario(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronCompraUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code ACTUALIZADO_COMPRADO} para el usuario
     * que tiene comprado el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación
     * para el usuario autenticado, asociándola al patrón especificado por el parámetro y
     * el creador del patrón.
     *
     * @param idPatron ID del patrón que ha modificado el creador. Se usa para asociar el
     *                 patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/actualizadoComprados")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoComprados(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoComprados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code ACTUALIZADO_COMPRADO} para el usuario
     * que tiene guardado el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación
     * para el usuario autenticado, asociándola al patrón especificado por el parámetro y
     * el creador del patrón.
     *
     * @param idPatron ID del patrón que ha modificado el creador. Se usa para asociar el
     *                 patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/actualizadoGuardados")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoGuardados(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoGuardados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code CAMBIO_PRECIO} para el usuario
     * que tiene guardado el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación
     * para el usuario autenticado, asociándola al patrón especificado por el parámetro y
     * el creador del patrón.
     *
     * @param idPatron ID del patrón que se ha cambiado de precio. Se usa para asociar el
     *                 patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/actualizadoPrecio")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoPrecio(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoPrecio");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code PATRON_GRATIS} para el usuario
     * que tiene guardado el patrón.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el creador del patron y crea una notificación
     * para el usuario autenticado, asociándola al patrón especificado por el parámetro y
     * el creador del patrón.
     *
     * @param idPatron ID del patrón que ahora es gratuito. Se usa para asociar el
     *                 patrón con la notificación.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/actualizadoGratis")
    public ResponseEntity<?> guardarNotificacionPatronActualizadoGratis(@RequestParam long idPatron) {
        logger.info("Entrando en el método guardarNotificacionPatronActualizadoGratis");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Crea y guarda una notificación de tipo {@code NUEVO_SEGUIDOR} para el usuario
     * creador que se ha empezado a seguir.
     * <p>
     * Verifica que el usuario esté autenticado mediante el componente
     * {@link Autenticacion}. Luego, obtiene el usuario creador que se ha empezado a seguir
     * y crea una notificación para este, asociándola al usuario autenticado.
     *
     * @param idCreador ID del usuario creador que se ha comenzado a seguir.
     * @return {@link ResponseEntity} con estado 200 OK la notificación se guarda correctamente,
     *         o un estado HTTP 401 si el usuario no está autenticado.
     */
    @PostMapping("/seguir")
    public ResponseEntity<?> guardarNotificacionSeguir(@RequestParam long idCreador) {
        logger.info("Entrando en el método guardarNotificacionPatronGuardadoCreador");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuarioQueSigueA = (Usuario) respuestaAutenticacion.getBody();

        Usuario creador = repositorioUsuario.getUsuarioById(idCreador);

        Notificacion notificacion = new Notificacion();

        notificacion.setUsuario(creador);
        notificacion.setTipo(Notificacion.TipoNotificacion.NUEVO_SEGUIDOR);
        notificacion.setUsuarioRelacionado(usuarioQueSigueA);

        repositorioNotificacion.save(notificacion);

        return ResponseEntity.ok().build();
    }

    /**
     * Marca una notificación como leída según su ID.
     * <p>
     * Verifica que el usuario esté autenticado mediante {@link Autenticacion}. Luego busca la
     * notificación por su ID y actualiza su estado a leída.
     *
     * @param idNotificacion ID de la notificación que se desea marcar como leída.
     * @return {@link ResponseEntity} con estado 200 OK si la notificación se actualiza correctamente,
     *         estado HTTP 401 si el usuario no está autenticado, o un estado 404 si la notificación no existe.
     */
    @PostMapping("/marcar-leido")
    public ResponseEntity<?> marcarLeido(@RequestParam long idNotificacion) {

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Optional<Notificacion> notif = repositorioNotificacion.findByIdNotificacion(idNotificacion);
        notif.get().setLeido(true);
        repositorioNotificacion.save(notif.get());
        return ResponseEntity.ok().build();
    }

}
