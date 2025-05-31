package com.tfg.tfgwebapp.controladores;

import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.servicios.ServicioPatron;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api/patrones")
public class ControladorPatrones {
    private static final Logger logger = LoggerFactory.getLogger(ControladorPatrones.class);

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioPatron repositorioPatron;
    private final ServicioPatron servicioPatron;
    private final ControladorNotificaciones controladorNotificaciones;

    // Inyección mediante constructor (recomendado)
    @Autowired
    public ControladorPatrones(RepositorioUsuario repositorioUsuario, RepositorioPatron repositorioPatron, ServicioPatron servicioPatron, ControladorNotificaciones controladorNotificaciones) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPatron = repositorioPatron;
        this.servicioPatron = servicioPatron;
        this.controladorNotificaciones = controladorNotificaciones;
    }

    @GetMapping("/encontrar")
    public ResponseEntity<Patron> encontrarPatron(@RequestParam long id) {
        try {
            Patron patron = (Patron) repositorioPatron.findPatronById(id);

            // FORZAR LA CARGA DEL CREADOR
            Hibernate.initialize(patron.getCreador());
            patron.getCreador().getNombreUsuario();

            // FORZAR LA CARGA DE LAS RESEÑAS
            Hibernate.initialize(patron.getReviews());
            patron.getReviews();

            System.out.println("En encontrar patron, response.ok");
            return ResponseEntity.ok(patron);
        } catch (Exception e) {
            System.out.println("Error al encontar el patron");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllPatronesPublicados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> getAllPatronesPublicados() {
        System.out.println("En controlador getAllPatronesPublicados");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        List<Patron> patrones = new ArrayList<>();
        try {
            patrones = repositorioPatron.findAllByEstado(Patron.Estado.Publicado);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getMejorValorados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> getTop10PatronesMejorValorados() {
        System.out.println("En controlador getMejorValorados");

        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();
        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<Patron> mejoresPatrones = repositorioPatron.findAllOrderByPuntuacionMediaDesc();

            // Limitar a los 10 primeros si hay más
            if (mejoresPatrones.size() > 10) {
                mejoresPatrones = mejoresPatrones.subList(0, 10);
            }

            return ResponseEntity.ok(mejoresPatrones);

        } catch (Exception e) {
            System.out.println("Error al obtener los patrones mejor valorados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getUltimosPatrones")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> getUltimos20Patrones() {
        System.out.println("En controlador getUltimosPatrones");

        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();
        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Pageable top20 = PageRequest.of(0, 20);
            List<Patron> ultimosPatrones = repositorioPatron.findTop20ByEstadoPublicadoOrderByIdDesc(top20);
            return ResponseEntity.ok(ultimosPatrones);
        } catch (Exception e) {
            System.out.println("Error al obtener los últimos patrones publicados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/getAllFiltros")
    @EntityGraph(attributePaths = {"reviews", "tags"})
    public ResponseEntity<List<Patron>> getAllFiltros(
            @RequestParam String filtros
    ) {
        System.out.println("En controlador getAllFiltros");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = repositorioUsuario.findByEmail(((UserDetails) auth.getPrincipal()).getUsername())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String[] flags = filtros.split(",");
        boolean principiante = Boolean.parseBoolean(flags[0]);
        boolean intermedio   = Boolean.parseBoolean(flags[1]);
        boolean avanzado     = Boolean.parseBoolean(flags[2]);
        boolean puntuacion = Boolean.parseBoolean(flags[3]);

        try {
        String[] filtroStrings = filtros.split(",");
        if (filtroStrings.length != 4) {
            return ResponseEntity.badRequest().build();
        }

        boolean[] filtroBools = new boolean[4];
        for (int i = 0; i < 4; i++) {
            filtroBools[i] = Boolean.parseBoolean(filtroStrings[i].trim());
        }

        List<Patron.Dificultad> dificultadesFiltradas = new ArrayList<>();
        if (filtroBools[0]) dificultadesFiltradas.add(Patron.Dificultad.Principiante);
        if (filtroBools[1]) dificultadesFiltradas.add(Patron.Dificultad.Intermedio);
        if (filtroBools[2]) dificultadesFiltradas.add(Patron.Dificultad.Avanzado);

        List<Patron> patrones;

        if (!dificultadesFiltradas.isEmpty()) {
            if (filtroBools[3]) {
                patrones = repositorioPatron.findAllByDificultadInOrderByPuntuacionMediaDesc(dificultadesFiltradas);
            } else {
                patrones = repositorioPatron.findAllByDificultadIn(dificultadesFiltradas);
            }
        } else {
            if (filtroBools[3]) {
                patrones = repositorioPatron.findAllOrderByPuntuacionMediaDesc();
            } else {
                patrones = repositorioPatron.findAll();
            }
        }

        return ResponseEntity.ok(patrones);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllBusqueda")
    @EntityGraph(attributePaths = {"reviews", "tags"})
    public ResponseEntity<List<Patron>> getAllBusqueda(
            @RequestParam String query
    ) {
        System.out.println("En controlador getAllBusqueda");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = repositorioUsuario.findByEmail(((UserDetails) auth.getPrincipal()).getUsername())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            List<Patron> patrones = repositorioPatron.searchByTituloAutorOTags(query.trim());
            System.out.println("Patrones en lista: " + patrones);
            return ResponseEntity.ok(patrones);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/getAllBusquedaFiltros")
    @EntityGraph(attributePaths = {"reviews", "tags"})
    public ResponseEntity<List<Patron>> buscarConFiltros(
            @RequestParam String query,
            @RequestParam String filtros
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = repositorioUsuario.findByEmail(((UserDetails) auth.getPrincipal()).getUsername())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            if (query == null || query.trim().isEmpty() || filtros == null) {
                return ResponseEntity.badRequest().build();
            }

            String[] filtroStrings = filtros.split(",");
            if (filtroStrings.length != 4) {
                return ResponseEntity.badRequest().build();
            }

            boolean[] filtroBools = new boolean[4];
            for (int i = 0; i < 4; i++) {
                filtroBools[i] = Boolean.parseBoolean(filtroStrings[i].trim());
            }

            List<Patron.Dificultad> dificultadesFiltradas = new ArrayList<>();
            if (filtroBools[0]) dificultadesFiltradas.add(Patron.Dificultad.Principiante);
            if (filtroBools[1]) dificultadesFiltradas.add(Patron.Dificultad.Intermedio);
            if (filtroBools[2]) dificultadesFiltradas.add(Patron.Dificultad.Avanzado);

            List<Patron> patrones;

            if (!dificultadesFiltradas.isEmpty()) {
                if (filtroBools[3]) {
                    patrones = repositorioPatron.buscarPorTextoYFiltrosOrdenado(query, dificultadesFiltradas);
                } else {
                    patrones = repositorioPatron.buscarPorTextoYFiltros(query, dificultadesFiltradas);
                }
            } else {
                // Sin filtros de dificultad, usar búsqueda general
                patrones = repositorioPatron.buscarPorTexto(query);
            }

            return ResponseEntity.ok(patrones);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/estado-publicacion")
    public ResponseEntity<?> cambiarEstadoPublicacion(
            @RequestParam long id
            //@RequestParam Patron.Estado nuevoEstado
    ) {
        try {
            Patron patron = (Patron) repositorioPatron.findPatronById(id);
            boolean enUso = servicioPatron.patronTieneUsuarios(id);
            System.out.println("En uso?: " + enUso);
            Patron.Estado estadoActual = patron.getEstado();

            if (enUso && estadoActual.equals(Patron.Estado.Publicado)){
                System.out.println("Patron publicado - conflicto");
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El patrón tiene usuarios que lo han comprado o empezado");
            } if (!enUso && estadoActual.equals(Patron.Estado.Publicado)){
                System.out.println("Patron publicado - sin comflicto");
                servicioPatron.cambiarEstadoPatron(id, Patron.Estado.Borrador);
            } else { //Estado = Borrador
                System.out.println("Patron borrador");
                servicioPatron.cambiarEstadoPatron(id, Patron.Estado.Publicado);
            }

            // FORZAR LA CARGA DEL CREADOR
            Hibernate.initialize(patron.getCreador());
            patron.getCreador().getNombreUsuario();

            return ResponseEntity.ok(patron);

            //return ResponseEntity.ok(patron);
        } catch (Exception e) {
            System.out.println("Error al cambiar el estado del patron");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/estado-inactivo")
    public ResponseEntity<?> cambiarEstadoInactivo(@RequestParam Long id) {
        servicioPatron.cambiarEstadoPatron(id, Patron.Estado.Inactivo);
        return ResponseEntity.ok("Estado cambiado a inactivo");
    }

    @PostMapping("/eliminar")
    public ResponseEntity<?> eliminarPatron(@RequestParam long id) {
        try {
            Patron patron = (Patron) repositorioPatron.findPatronById(id);
            boolean enUso = servicioPatron.patronTieneUsuarios(id);

            if (patron == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            if (enUso) {
                // Devolver código y mensaje especial para alertar en frontend
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("El patrón tiene usuarios que lo han comprado o empezado");
            } else {
                controladorNotificaciones.guardarNotificacionPatronEliminado(id);
                servicioPatron.eliminarPatron(id);
                return ResponseEntity.ok("Patrón eliminado");
            }

        } catch (Exception e) {
            System.out.println("Error al eliminar el patrón con ID: " + id);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patrones-tienda-publicados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesUsuarioPublicados() {
        System.out.println("En controlador obtenerPatronesUsuario");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        List<Patron> patrones = new ArrayList<>();
        try {
            patrones = repositorioPatron.findPatronByCreadorAndEstado(usuario, Patron.Estado.Publicado);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patrones-tienda-borradores")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesUsuarioBorradores() {
        System.out.println("En controlador obtenerPatronesUsuario");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        List<Patron> patrones = new ArrayList<>();
        try {
            patrones = repositorioPatron.findPatronByCreadorAndEstado(usuario, Patron.Estado.Borrador);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patrones-tienda-inactivos")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesUsuarioInactivos() {
        System.out.println("En controlador obtenerPatronesUsuario");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        List<Patron> patrones = new ArrayList<>();
        try {
            patrones = repositorioPatron.findPatronByCreadorAndEstado(usuario, Patron.Estado.Inactivo);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/patrones-tienda-otro")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesOtrosPublicados(
            @RequestParam Long otroUsuario
    ) {
        System.out.println("En controlador obtenerPatronesUsuario");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        List<Patron> patrones = new ArrayList<>();
        Usuario creador = repositorioUsuario.findById(otroUsuario).get();
        try {
            patrones = repositorioPatron.findPatronByCreadorAndEstado(creador, Patron.Estado.Publicado);
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/nuevo")
    public ResponseEntity<Patron> crearPatron(
            @RequestParam Long idCreador
    ) {
        Patron patron = new Patron();
        patron.setCreador(repositorioUsuario.findById(idCreador).get());
        repositorioPatron.save(patron);
        return ResponseEntity.status(HttpStatus.CREATED).body(patron);
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardarPatron(
            @RequestParam Long idPatron,
            @RequestParam String titulo,
            @RequestParam double precio,
            @RequestParam Patron.Dificultad dificultad,
            @RequestParam String descripcion,
            @RequestParam Patron.Idioma idioma,
            @RequestParam Patron.Unidad unidad,
            @RequestParam String lanas,
            @RequestParam String agujaGanchillo,
            @RequestParam String agujaLanera,
            @RequestParam String otros,
            @RequestParam String abreviaturas,
            @RequestParam(required = false) List<MultipartFile> imagenes,
            @RequestParam List<String> tags,
            @RequestParam String instrucciones

            ) throws IOException {
        logger.info("Entrando en el método guardarPatron");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }


        Patron patron = (Patron) repositorioPatron.findPatronById(idPatron);

        // Define ruta y nombre para la carpeta de destino de las imagenes de los patrones
        String carpetaDestino = "src/main/resources/static/imagenes/patrones/";
        List<String> rutasRelativas = new ArrayList<>();
        if (imagenes != null && !imagenes.isEmpty()) {
            try {
                for (MultipartFile imagen : imagenes) {
                    if (!imagen.isEmpty()) {
                        String nombreArchivo = System.currentTimeMillis() + "_" + imagen.getOriginalFilename();
                        Path rutaArchivo = Paths.get(carpetaDestino, nombreArchivo);
                        Files.write(rutaArchivo, imagen.getBytes());

                        // Ruta relativa para guardar en el objeto Patron
                        String rutaRelativa = "./imagenes/patrones/" + nombreArchivo;
                        rutasRelativas.add(rutaRelativa);
                    }
                }
                patron.setImagenes(rutasRelativas);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar las imágenes.");
            }
        }


        patron.setTitulo(titulo);
        patron.setPrecio(precio);
        patron.setDificultad(dificultad);
        patron.setDescripcion(descripcion);
        patron.setIdioma(idioma);
        patron.setUnidad(unidad);
        patron.setLanas(lanas);
        patron.setAgujaGanchillo(agujaGanchillo);
        patron.setAgujaLanera(agujaLanera);
        patron.setOtros(otros);
        patron.setAbreviaturas(abreviaturas);
        patron.setTags(tags);
        patron.setInstrucciones(instrucciones);

        repositorioPatron.save(patron);

        return ResponseEntity.ok(patron);
    }

    @GetMapping("/estaGuardo")
    public ResponseEntity<?> estaGuardo(@RequestParam Long idPatron) {
        logger.info("Entrando en el método estaGuardado");
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

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();

        if (usuario.getPatronesGuardados().contains(patron)) {
            return ResponseEntity.ok(true);
        } else{
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/guardarBiblioteca")
    public ResponseEntity<?> guardarBiblioteca(@RequestParam Long idPatron) {
        logger.info("Entrando en el método guardar");
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

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            System.out.println("Patron no encontrado");
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();
        System.out.println("Patron encontrado: " + patron);

        if (!usuario.getPatronesGuardados().contains(patron)) {
            usuario.getPatronesGuardados().add(patron);
            System.out.println("Patrones guardados tras añadir: " + usuario.getPatronesGuardados());
        }else{
            usuario.getPatronesGuardados().remove(patron);
            System.out.println("Patrones guardados tras quitar: " + usuario.getPatronesGuardados());
        }

        repositorioUsuario.save(usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patrones-biblioteca-guardados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesBibliotecaGuardados() {
        System.out.println("En controlador obtenerPatronesBibliotecaGuardados");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        try {
            List<Patron> patrones = usuario.getPatronesGuardados();
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones guardados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/estaComprado")
    public ResponseEntity<?> estaComprado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método estaComprado");
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

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();

        if (usuario.getPatronesComprados().contains(patron)) {
            return ResponseEntity.ok(true);
        } else{
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/guardarComprado")
    public ResponseEntity<?> guardarComprado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método guardarComprado");
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

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            System.out.println("Patron no encontrado");
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();
        System.out.println("Patron encontrado: " + patron);

        // Guardamos el paton en la lista de patrones comprados
        usuario.getPatronesComprados().add(patron);
        System.out.println("Patrones guardados tras añadir: " + usuario.getPatronesGuardados());

        repositorioUsuario.save(usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patrones-biblioteca-comprados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<List<Patron>> obtenerPatronesBibliotecaComprados() {
        System.out.println("En controlador obtenerPatronesBibliotecaComprados");
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();

        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserDetails userDetails = (UserDetails) usuarioAuth.getPrincipal();
        Optional<Usuario> usuarioOpt = repositorioUsuario.findByEmail(userDetails.getUsername());

        if (!usuarioOpt.isPresent()) {
            System.out.println("Usuario no encontrado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Usuario usuario = usuarioOpt.get();

        try {
            List<Patron> patrones = usuario.getPatronesComprados();
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones guardados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
