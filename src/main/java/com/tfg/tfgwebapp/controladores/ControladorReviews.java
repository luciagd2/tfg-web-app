package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Review;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioReview;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.seguridad.Autenticacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ControladorReviews {
    private static final Logger logger = LoggerFactory.getLogger(ControladorPatrones.class);

    private final RepositorioReview repositorioReview;
    private final RepositorioPatron repositorioPatron;
    private final RepositorioUsuario repositorioUsuario;
    private final Autenticacion autenticacion;

    // Inyección mediante constructor (recomendado)
    @Autowired
    public ControladorReviews(RepositorioReview repositorioReview,
                              RepositorioPatron repositorioPatron,
                              RepositorioUsuario repositorioUsuario,
                              Autenticacion autenticacion
    ) {
        this.repositorioReview = repositorioReview;
        this.repositorioPatron = repositorioPatron;
        this.repositorioUsuario = repositorioUsuario;
        this.autenticacion = autenticacion;
    }

    @GetMapping("/getReviews")
    public ResponseEntity<?> getReviews(@RequestParam long patronId) {

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        try {
            List<Review> reviews = repositorioReview.findReviewsByPatronId(patronId);

            System.out.println("En encontrar reviews, response.ok: "+ reviews);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            System.out.println("Error al encontar las reviews");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/nuevaReview")
    public ResponseEntity<?> guardarNuevaReview(
            @RequestParam long idPatron,
            @RequestParam int puntuacion,
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) MultipartFile imagen
    ) throws IOException {

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();
        Review nuevaReview = new Review();
        Patron patron = repositorioPatron.findById(idPatron).get();

        nuevaReview.setPatron(patron);
        nuevaReview.setPuntuacion(puntuacion);
        nuevaReview.setUsuario(usuario);
        if (imagen != null && !imagen.isEmpty()) {
            // Define ruta y nombre
            String carpetaDestino = "src/main/resources/static/imagenes/reviews/";
            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());
            // Guarda la ruta relativa en el usuario
            nuevaReview.setImagen("./imagenes/reviews/" + nombreArchivo);
        }
        if (mensaje != null && !mensaje.isEmpty()) {
            nuevaReview.setMensaje(mensaje);
        }

        Review guardada = repositorioReview.save(nuevaReview);
        return ResponseEntity.ok(guardada);

    }
}
