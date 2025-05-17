package com.tfg.tfgwebapp.controladores;

import org.springframework.ui.Model;
import com.tfg.tfgwebapp.modelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.servicios.ServicioAutenticacion;
import com.tfg.tfgwebapp.servicios.ServiciosUsuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/usuarios")
public class ControladorUsuario {
    private final RepositorioUsuario repositorioUsuario;
    private final ServiciosUsuario serviciosUsuario;
    private final ServicioAutenticacion servicioAutenticacion;

    @Autowired
    public ControladorUsuario(RepositorioUsuario repositorioUsuario, ServiciosUsuario serviciosUsuario, ServicioAutenticacion servicioAutenticacion) {
        this.repositorioUsuario = repositorioUsuario;
        this.serviciosUsuario = serviciosUsuario;
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/registro")
    public ResponseEntity<String> registrar(@RequestBody Usuario usuario) {
        boolean registrado = serviciosUsuario.registrar(usuario);
        if (registrado) {
            return ResponseEntity.ok("Usuario registrado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }
    }

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

            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }
    }


    @PostMapping("/perfil")
    public ResponseEntity<?> completarPerfil(
            @RequestParam String username,
            @RequestParam String tipoPerfil,
            @RequestParam(required = false) MultipartFile imagen
    ) {
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
        usuario.setTipoPerfil(tipoPerfil);

        if (imagen != null && !imagen.isEmpty()) {
            try {
                usuario.setImagenPerfil(Arrays.toString(imagen.getBytes()));
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la imagen");
            }
        }

        repositorioUsuario.save(usuario);

        return ResponseEntity.ok("Perfil actualizado correctamente");
    }

    @PostMapping("/perfil/guardar")
    public String guardarPerfil(@RequestParam("imagen") MultipartFile imagen,
                                @RequestParam("nombreUsuario") String nombreUsuario,
                                @RequestParam("email") String email,
                                Model model) {
        try {
            // 1. Define ruta para guardar la imagen (puede ser carpeta en tu proyecto o ruta absoluta)
            String carpetaDestino = "imagenes/perfiles/";

            // Asegúrate que la carpeta exista (crearla si no)
            File directorio = new File(carpetaDestino);
            if (!directorio.exists()) {
                directorio.mkdirs();
            }

            // 2. Crea un nombre único para la imagen para evitar sobreescritura
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();

            // 3. Guarda la imagen en disco
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());

            // 4. Guarda solo la ruta relativa en la base de datos
            String rutaRelativa = carpetaDestino + nombreArchivo;

            // 5. Crea o recupera el usuario, asigna la ruta de la imagen y demás datos
            Usuario usuario = new Usuario();
            usuario.setNombreUsuario(nombreUsuario);
            usuario.setEmail(email);
            usuario.setImagenPerfil(rutaRelativa);

            // Aquí guardar el usuario usando tu servicio o repositorio
            repositorioUsuario.save(usuario);

            model.addAttribute("mensaje", "Perfil guardado correctamente");
            return "exito";

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al guardar la imagen");
            return "error";
        }
    }

}

