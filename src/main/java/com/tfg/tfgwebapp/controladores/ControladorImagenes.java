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

@RestController
@RequestMapping("/api/imagenes")
public class ControladorImagenes {
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
