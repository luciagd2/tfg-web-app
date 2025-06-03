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
import java.util.Collections;
import java.util.List;

/**
 * Controlador REST para gestionar las reviews (valoraciones y comentarios) de los patrones.
 *
 * <p>Este controlador ofrece endpoints para:
 * <lu>
 *   <li>Obtener las reviews asociadas a un patrón específico.</li>
 *   <li>Crear una nueva review para un patrón.</li>
 * </lu>
 *
 * <p>Todos los endpoints requieren autenticación previa del usuario mediante el componente {@link Autenticacion}.
 *
 * <p>Inyecta los repositorios necesarios para acceder a datos de Review, Patron y Usuario.
 */
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

    /**
     * Obtiene la lista de reviews asociadas a un patrón identificado por su ID.
     * Las reviews se devuelven en orden inverso (las más recientes primero).
     *
     * @param patronId ID del patrón del cual obtener las reviews.
     * @return ResponseEntity con la lista de reviews o error si no está autenticado o falla la consulta.
     */
    @GetMapping("/getReviews")
    public ResponseEntity<?> getReviews(@RequestParam long patronId) {

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        try {
            List<Review> reviews = repositorioReview.findReviewsByPatronId(patronId);
            if(reviews != null && !reviews.isEmpty()) {
                Collections.reverse(reviews);
            }
            System.out.println("En encontrar reviews, response.ok: "+ reviews);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            System.out.println("Error al encontar las reviews");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Crea y guarda una nueva review para un patrón.
     * Permite incluir puntuación, mensaje opcional e imagen opcional.
     *
     * @param idPatron ID del patrón al que se asocia la review.
     * @param puntuacion Puntuación numérica asignada en la review.
     * @param mensaje Mensaje o comentario opcional de la review.
     * @param imagen Imagen opcional asociada a la review (multipart file).
     * @return ResponseEntity con la review guardada o error si falla autenticación o guardado.
     * @throws IOException si hay error al guardar la imagen en el sistema de archivos.
     */
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
