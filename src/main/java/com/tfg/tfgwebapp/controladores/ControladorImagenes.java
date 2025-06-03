package com.tfg.tfgwebapp.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestionar la subida de imágenes asociadas a los patrones.
 *
 * <p>Permite a los usuarios subir archivos de imagen que luego podrán asociarse
 * a los patrones creados por ellos en la plataforma.
 *
 * <p>Las imágenes se almacenan en una carpeta del sistema de archivos local, y se devuelve
 * una URL relativa que puede ser usada por el cliente para mostrar la imagen.
 */
@RestController
@RequestMapping("/api/imagenes")
public class ControladorImagenes {

    /**
     * Endpoint para subir una imagen.
     *
     * <p>El archivo se guarda en la carpeta local definida, y se genera un nombre único
     * basado en la hora actual en milisegundos para evitar colisiones de nombres.
     *
     * @param imagen Imagen enviada por el cliente en formato multipart.
     * @return ResponseEntity con la URL relativa de la imagen guardada, o error si falla la operación.
     */
    @PostMapping("/subir")
    public ResponseEntity<?> subirImagen(@RequestParam("file") MultipartFile imagen) {
        try {
            String carpetaDestino = "src/main/resources/static/imagenes/patrones/";

            String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
            Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
            Files.write(rutaArchivo, imagen.getBytes());

            Map<String, String> response = new HashMap<>();
            response.put("url", "imagenes/patrones/" + nombreArchivo);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al subir la imagen");
        }
    }
}
