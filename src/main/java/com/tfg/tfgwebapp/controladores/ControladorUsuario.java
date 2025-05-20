package com.tfg.tfgwebapp.controladores;

import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import com.tfg.tfgwebapp.modelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.servicios.ServicioAutenticacion;
import com.tfg.tfgwebapp.servicios.ServiciosUsuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;

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

}

