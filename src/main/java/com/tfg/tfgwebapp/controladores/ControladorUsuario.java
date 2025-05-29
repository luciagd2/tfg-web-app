package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.servicios.ServicioAutenticacion;
import com.tfg.tfgwebapp.servicios.ServiciosUsuario;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/usuarios")
public class ControladorUsuario {
    //TODO: borrar, es solo para mensajes de log
    private static final Logger logger = LoggerFactory.getLogger(ControladorUsuario.class);

    private final RepositorioUsuario repositorioUsuario;
    private final ServiciosUsuario serviciosUsuario;
    private final ServicioAutenticacion servicioAutenticacion;

    @Autowired
    public ControladorUsuario(RepositorioUsuario repositorioUsuario, ServiciosUsuario serviciosUsuario, ServicioAutenticacion servicioAutenticacion) {
        this.repositorioUsuario = repositorioUsuario;
        this.serviciosUsuario = serviciosUsuario;
        this.servicioAutenticacion = servicioAutenticacion;
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
        System.out.println("Usuario: "+user);
        System.out.println("Usuario presente: "+user.isPresent());

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
     * Este método permite al usuario establecer un nombre de usuario personalizado,
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

        // Ahora actualizas el usuario con los datos recibidos
        usuario.setNombreUsuario(username);
        usuario.setEsCreador(esCreador);

        if (imagen != null && !imagen.isEmpty()) {
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
     * Este endpoint permite modificar opcionalmente el nombre de usuario, la descripción
     * y la imagen de perfil. Solo los campos enviados serán actualizados; los que no se
     * incluyan en la petición permanecerán sin cambios.
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
                                @RequestParam(required = false) MultipartFile imagen,
                                @RequestParam(required = false) String descripcionUsuario
    ) throws IOException {
        logger.info("Entrando en el método guardarPerfil");
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

        // Ahora actualizas el usuario con los datos recibidos
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            usuario.setNombreUsuario(nombreUsuario);
        }
        if (imagen != null && !imagen.isEmpty()) {
            // Define ruta y nombre
            String carpetaDestino = "src/main/resources/static/imagenes/perfiles/";
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());
            // Guarda la ruta relativa en el usuario
            usuario.setImagenPerfil("./imagenes/perfiles/" + nombreArchivo);
        }
        if (descripcionUsuario != null && !descripcionUsuario.isEmpty()) {
            usuario.setDescripcionUsuario(descripcionUsuario);
        }
        repositorioUsuario.save(usuario);

        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/perfil/encontrar")
    public ResponseEntity<?> encontrarUsuario(@RequestParam Long idUsuario
    ) throws IOException {
        logger.info("Entrando en el método encontrarUsuario");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        Usuario usuario = repositorioUsuario.findById(idUsuario).get();
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/seguimiento")
    public ResponseEntity<?> seguimientoUsuario(@RequestParam Long idCreador) {
        logger.info("Entrando en el método seguimientoUsuario");
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

    @GetMapping("/sigueA")
    public ResponseEntity<?> sigueA(@RequestParam Long idCreador) {
        logger.info("Entrando en el método sigueA");
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

        Optional<Usuario> creadorOpt = repositorioUsuario.findById(idCreador);

        if (creadorOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Usuario creador = creadorOpt.get();
/*
        if (!creador.getIdsSeguidores().contains(usuario.getId())) {
            creador.getIdsSeguidores().add(usuario.getId());
            repositorioUsuario.save(creador);
        } else {
            creador.getIdsSeguidores().remove(usuario.getId());
            repositorioUsuario.save(creador);
        }
*/
        if (creador.getIdsSeguidores().contains(usuario.getId())) {
            return ResponseEntity.ok(true);
        } else{
            return ResponseEntity.ok(false);
        }
    }
}

