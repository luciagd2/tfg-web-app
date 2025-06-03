package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.seguridad.Autenticacion;
import com.tfg.tfgwebapp.servicios.ServicioAutenticacion;
import com.tfg.tfgwebapp.servicios.ServiciosUsuario;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para la gestión de usuarios.
 * <p>
 * Proporciona endpoints para:
 * <ul>
 *     <li>Autenticar al usuario y generar su sesión</li>
 *     <li>Registar un nuevo usuario y completar su perfil</li>
 *     <li>Actualizar los datos del perfil de un usuario</li>
 *     <li>Encontrar usuario</li>
 *     <li>Gestionar el seguimiento entre usuarios</li>
 *     <li>Encontra los seguidores de un usuario</li>
 *     <li>Comprobar si un usuario es seguidor de otro</li>
 * </ul>
 *
 * Todos los métodos, a excepción de {@link #login(Map, HttpServletRequest)}, requieren que el
 * usuario esté autenticado. La autenticación se gestiona mediante el componente {@link Autenticacion}.
 */
@RestController
@RequestMapping("/api/usuarios")
public class ControladorUsuario {
    //TODO: borrar, es solo para mensajes de log
    private static final Logger logger = LoggerFactory.getLogger(ControladorUsuario.class);

    private final RepositorioUsuario repositorioUsuario;
    private final ServiciosUsuario serviciosUsuario;
    private final ServicioAutenticacion servicioAutenticacion;
    private final Autenticacion autenticacion;

    @Autowired
    public ControladorUsuario(RepositorioUsuario repositorioUsuario, ServiciosUsuario serviciosUsuario, ServicioAutenticacion servicioAutenticacion, Autenticacion autenticacion) {
        this.repositorioUsuario = repositorioUsuario;
        this.serviciosUsuario = serviciosUsuario;
        this.servicioAutenticacion = servicioAutenticacion;
        this.autenticacion = autenticacion;
    }

    /**
     * Maneja la solicitud de inicio de sesión de un usuario.
     * <p>
     * Verifica las credenciales recibidas (email y password), y si son válidas,
     * establece la autenticación en el contexto de seguridad de Spring. En caso contrario,
     * retorna un error HTTP 401.
     *
     * @param datos   Mapa que contiene las credenciales del usuario. Incluye:
     *                "email": correo del usuario,
     *                "password": contraseña del usuario
     * @param request Objeto HttpServletRequest para acceder a la sesión y almacenar el contexto de seguridad
     * @return Una respuesta HTTP 200 con el objeto {@link Usuario} autenticado si las credenciales son válidas,
     *         o una respuesta HTTP 401 con un mensaje de error si son inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> datos, HttpServletRequest request) {
        Optional<Usuario> user = serviciosUsuario.login(datos.get("email"), datos.get("password"));

        if (user.isPresent()) {
            // Crea la autenticación con los detalles del usuario
            UserDetails userDetails = servicioAutenticacion.loadUserByUsername(datos.get("email"));
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // Setea la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Guarda el contexto de seguridad en la sesión HTTP
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            System.out.println("Respuesta login ok");
            return ResponseEntity.ok(user.get());
        } else {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }

    /**
     * Maneja la solicitud de registro de un nuevo usuario.
     * <p>
     * Si el registro es exitoso, autentica automáticamente al usuario y guarda el
     * contexto de seguridad en la sesión HTTP. En caso de que el usuario ya exista,
     * devuelve una respuesta de conflicto.
     *
     * @param usuario Objeto {@link Usuario} recibido en el cuerpo de la petición. Debe contener
     *                los datos necesarios para el registro (nombre, email, contraseña, etc.).
     * @param request Objeto HttpServletRequest utilizado para establecer la sesión del usuario registrado.
     * @return Una respuesta HTTP 200 si el usuario se registró y autenticó correctamente,
     *         o HTTP 409 (Conflict) si el usuario ya existe en la base de datos.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario, HttpServletRequest request) {
        boolean registrado = serviciosUsuario.registrar(usuario);
        if (registrado) {
            // Autenticar al usuario después del registro
            UserDetails userDetails = servicioAutenticacion.loadUserByUsername(usuario.getEmail());
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            request.getSession(true).setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return ResponseEntity.ok("Usuario registrado y autenticado correctamente");

        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }
    }

    /**
     * Completa el perfil del usuario autenticado.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, permite al usuario establecer un nombre de usuario personalizado,
     * indicar si es creador y, opcionalmente, subir una imagen de perfil. La imagen se guarda
     * localmente y su ruta relativa se almacena en el objeto {@link Usuario}.
     *
     * @param username   El nombre de usuario a establecer para el perfil.
     * @param esCreador  Booleano que indica si el usuario desea marcarse como creador.
     * @param imagen     (Opcional) Archivo de imagen para usar como foto de perfil.
     * @return Una respuesta HTTP 200 con el usuario actualizado si la operación es exitosa,
     *         o HTTP 401 si el usuario no está autenticado o no existe en la base de datos.
     * @throws IOException Si ocurre un error al guardar el archivo de imagen en el sistema.
     */
    @PostMapping("/perfil")
    public ResponseEntity<?> completarPerfil(
            @RequestParam String username,
            @RequestParam boolean esCreador,
            @RequestParam(required = false) MultipartFile imagen
    ) throws IOException {
        logger.info("Entrando en el método completarPerfil");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        // Ahora actualizas el usuario con los datos recibidos
        usuario.setNombreUsuario(username);
        usuario.setEsCreador(esCreador);

        if (imagen != null && !imagen.isEmpty()) {
            System.out.println("Guardando imagen");
            // Define ruta y nombre
            String carpetaDestino = "src/main/resources/static/imagenes/perfiles/";
            // TODO: borrar la creacion de la carpeta, ya existe
            File dir = new File(carpetaDestino);
            if (!dir.exists()) dir.mkdirs();

            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());

            // Guarda la ruta relativa en el usuario
            usuario.setImagenPerfil("./imagenes/perfiles/" + nombreArchivo);
        }

        repositorioUsuario.save(usuario);

        return ResponseEntity.ok(usuario);
    }

    /**
     * Actualiza el perfil del usuario autenticado.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, permite modificar opcionalmente el
     * nombre de usuario, la descripción y la imagen de perfil. Solo los campos enviados
     * serán actualizados; los que no se incluyan en la petición permanecerán sin cambios.
     *
     * @param nombreUsuario       (Opcional) Nuevo nombre de usuario a guardar.
     * @param imagen              (Opcional) Nueva imagen de perfil. Se guarda localmente y se almacena la ruta.
     * @param descripcionUsuario  (Opcional) Nueva descripción personalizada del usuario.
     * @return Una respuesta HTTP 200 con el objeto {@link Usuario} actualizado si la operación es exitosa,
     *         o HTTP 401 si el usuario no está autenticado o no se encuentra en la base de datos.
     * @throws IOException Si ocurre un error al guardar la imagen en el sistema de archivos.
     */
    @PostMapping("/perfil/guardar")
    public ResponseEntity<?> guardarPerfil(@RequestParam(required = false) String nombreUsuario,
                                           @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                                           @RequestParam(required = false) String descripcionUsuario
    ) throws IOException {
        logger.info("Entrando en el método guardarPerfil");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        // Ahora actualizas el usuario con los datos recibidos
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            usuario.setNombreUsuario(nombreUsuario);
        }
        System.out.println("Antes de comprobar imagen: " + imagen.getName());
        if (imagen != null && !imagen.isEmpty()) {
            System.out.println("Despues de comprobar imagen");
            // Define ruta y nombre
            String carpetaDestino = "src/main/resources/static/imagenes/perfiles/";

            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());
            // Guarda la ruta relativa en el usuario
            System.out.println("Nombre archivo: " + nombreArchivo);
            usuario.setImagenPerfil("imagenes/perfiles/" + nombreArchivo);
        }
        if (descripcionUsuario != null && !descripcionUsuario.isEmpty()) {
            usuario.setDescripcionUsuario(descripcionUsuario);
        }
        repositorioUsuario.save(usuario);

        return ResponseEntity.ok(usuario);
    }

    /**
     * Encuentra el usuario con el ID indicado.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, busca el usuario correspondiente al ID proporcionado.
     *
     * @param idUsuario ID del usuario que se desea encontrar.
     * @return {@link ResponseEntity} con los datos del usuario encontrado si existe y el solicitante está autenticado,
     *         {@link ResponseEntity} con estado 404 si el usuario no existe,
     *         o estado 401 si el solicitante no está autenticado.
     * @throws IOException en caso de error de entrada/salida (no se utiliza en la implementación actual, pero está declarado).
     */
    @GetMapping("/perfil/encontrar")
    public ResponseEntity<?> encontrarUsuario(@RequestParam Long idUsuario
    ) throws IOException {
        logger.info("Entrando en el método encontrarUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = repositorioUsuario.findById(idUsuario).get();

        return ResponseEntity.ok(usuario);
    }

    /**
     * Permite a un usuario autenticado seguir o dejar de seguir a un creador.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, comprueba si el usuario aun no
     * sigue al creador, y en este caso se añade su ID a la lista de seguidores del creador.
     * Si ya lo sigue, se elimina.
     *
     * @param idCreador ID del usuario creador al que se desea seguir o dejar de seguir.
     * @return {@link ResponseEntity} con estado 200 OK si la operación se realiza correctamente,
     *         404 si el creador no existe,
     *         o 401 si el usuario no está autenticado.
     */
    @PostMapping("/seguimiento")
    public ResponseEntity<?> seguimientoUsuario(@RequestParam Long idCreador) {
        logger.info("Entrando en el método seguimientoUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        Optional<Usuario> creadorOpt = repositorioUsuario.findById(idCreador);

        if (creadorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario creador = creadorOpt.get();

        if (!creador.getIdsSeguidores().contains(usuario.getId())) {
            creador.getIdsSeguidores().add(usuario.getId());
        }else{
            creador.getIdsSeguidores().remove(usuario.getId());
        }

        repositorioUsuario.save(creador);
        return ResponseEntity.ok().build();
    }

    /**
     * Verifica si el usuario autenticado está siguiendo a un determinado creador.
     * <p>
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, consulta la lista de seguidores del
     * creador y determina si incluye al usuario autenticado.
     *
     * @param idCreador ID del creador que se desea comprobar si está siendo seguido.
     * @return {@link ResponseEntity} con:
     *         <ul>
     *             <li>{@code true} si el usuario está siguiendo al creador.</li>
     *             <li>{@code false} si no lo está siguiendo.</li>
     *             <li>404 Not Found si el creador no existe.</li>
     *             <li>401 Unauthorized si el usuario no está autenticado.</li>
     *         </ul>
     */
    @GetMapping("/sigueA")
    public ResponseEntity<?> sigueA(@RequestParam Long idCreador) {
        logger.info("Entrando en el método sigueA");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        Optional<Usuario> creadorOpt = repositorioUsuario.findById(idCreador);

        if (creadorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario creador = creadorOpt.get();
        if (creador.getIdsSeguidores().contains(usuario.getId())) {
            return ResponseEntity.ok(true);
        } else{
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Obtiene la lista de usuarios que siguen al usuario autenticado (creador).
     * <p>
     *
     * Verifica que el usuario esté autenticado utilizando el componente
     * {@link Autenticacion}. Si la autenticación falla, devuelve un estado HTTP 401.
     * Si el usuario está autenticado correctamente, devuelve la información de todos los
     * usuarios cuyos IDs estén registrados como seguidores del usuario actual.
     *
     * @return {@link ResponseEntity} con:
     *         <ul>
     *             <li>Una lista de usuarios que siguen al usuario autenticado, con estado 200 OK.</li>
     *             <li>401 Unauthorized si el usuario no está autenticado.</li>
     *         </ul>
     */
    @GetMapping("/seguidores")
    public ResponseEntity<?> getSeguidores() {
        logger.info("Entrando en el método getSeguidores");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

        List<Long> idsSeguidores = creador.getIdsSeguidores();

        return ResponseEntity.ok(repositorioUsuario.findAllById(idsSeguidores));
    }
}

