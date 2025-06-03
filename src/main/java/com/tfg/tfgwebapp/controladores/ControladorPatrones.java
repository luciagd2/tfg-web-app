package com.tfg.tfgwebapp.controladores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.tfg.tfgwebapp.clasesModelo.Patron;
import com.tfg.tfgwebapp.clasesModelo.Usuario;
import com.tfg.tfgwebapp.repositorios.RepositorioPatron;
import com.tfg.tfgwebapp.repositorios.RepositorioUsuario;
import com.tfg.tfgwebapp.seguridad.Autenticacion;
import com.tfg.tfgwebapp.servicios.ServicioPatron;
import jakarta.servlet.http.HttpServletRequest;
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

/**
 * Controlador REST para la gestión de patrones.
 * <p>
 * Proporciona endpoints para:
 * <ul> TODO:
 *     <li>Autenticar al usuario y generar su sesión</li>
 *     <li>Registar un nuevo usuario y completar su perfil</li>
 *     <li>Actualizar los datos del perfil de un usuario</li>
 *     <li>Encontrar usuario</li>
 *     <li>Gestionar el seguimiento entre usuarios</li>
 *     <li>Encontra los seguidores de un usuario</li>
 *     <li>Comprobar si un usuario es seguidor de otro</li>
 * </ul>
 *
 * Todos los métodos requieren que el usuario esté autenticado. La autenticación se gestiona mediante el componente {@link Autenticacion}.
 */
@RestController
@RequestMapping("/api/patrones")
public class ControladorPatrones {
    private static final Logger logger = LoggerFactory.getLogger(ControladorPatrones.class);

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioPatron repositorioPatron;
    private final ServicioPatron servicioPatron;
    private final ControladorNotificaciones controladorNotificaciones;
    private final Autenticacion autenticacion;

    // Inyección mediante constructor (recomendado)
    @Autowired
    public ControladorPatrones(RepositorioUsuario repositorioUsuario,
                               RepositorioPatron repositorioPatron,
                               ServicioPatron servicioPatron,
                               ControladorNotificaciones controladorNotificaciones,
                               Autenticacion autenticacion
    ) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioPatron = repositorioPatron;
        this.servicioPatron = servicioPatron;
        this.controladorNotificaciones = controladorNotificaciones;
        this.autenticacion = autenticacion;
    }

    /**
     * Busca un patrón por su ID y retorna sus datos completos.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recupera el patrón desde la base de datos usando su ID.</li>
     *   <li>Fuerza la carga del creador y de las reseñas asociadas al patrón
     *       para evitar problemas con la carga perezosa (lazy loading).</li>
     * </ul>
     *
     * @param id <b>(long)</b> ID del patrón a buscar. Se recibe como parámetro de la URL.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con el objeto <code>Patron</code> si se encuentra correctamente.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error al recuperar el patrón.</li>
     * </ul>
     */
    @GetMapping("/encontrar")
    public ResponseEntity<?> encontrarPatron(@RequestParam long id) {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }
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

    /**
     * Devuelve todos los patrones con estado "Publicado" del usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Consulta todos los patrones con estado <code>Publicado</code> y los devuelve.</li>
     *   <li>Incluye la carga anticipada de las reseñas asociadas mediante <code>@EntityGraph</code>.</li>
     *   <li>Devuelve una lista vacía si no hay patrones publicados.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de <code>Patron</code> si se obtienen correctamente.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error durante la consulta.</li>
     * </ul>
     */
    @GetMapping("/getAllPatronesPublicados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> getAllPatronesPublicados() {
        System.out.println("En controlador getAllPatronesPublicados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

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

    /**
     * Devuelve los 10 patrones publicados con mejor valoración.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Obtiene todos los patrones ordenados por puntuación media descendente.</li>
     *   <li>Devuelve como máximo los 10 primeros resultados.</li>
     *   <li>Utiliza <code>@EntityGraph</code> para cargar las reseñas asociadas.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con una lista de hasta 10 <code>Patron</code> ordenados por valoración.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error al obtener los datos.</li>
     * </ul>
     */
    @GetMapping("/getMejorValorados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> getTop10PatronesMejorValorados() {
        System.out.println("En controlador getMejorValorados");

        /*
        Authentication usuarioAuth = SecurityContextHolder.getContext().getAuthentication();
        if (usuarioAuth == null || !usuarioAuth.isAuthenticated() || usuarioAuth instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        */
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
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

    /**
     * Devuelve los últimos 20 patrones publicados, ordenados por ID descendente.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Consulta los 20 patrones más recientes con estado <code>Publicado</code>.</li>
     *   <li>Utiliza paginación con <code>PageRequest</code> y <code>@EntityGraph</code>
     *       para cargar anticipadamente las reseñas asociadas.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con una lista de los últimos 20 <code>Patron</code>.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error al obtener los datos.</li>
     * </ul>
     */
    @GetMapping("/getUltimosPatrones")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> getUltimos20Patrones() {
        System.out.println("En controlador getUltimosPatrones");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
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

    /**
     * Devuelve una lista de patrones filtrados por nivel de dificultad y/u
     * ordenados por puntuación.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recibe un string de filtros con 4 valores booleanos separados por comas.</li>
     *   <li>Filtra los patrones según los filtros seleccionados:
     *     <ul>
     *       <li><code>filtro[0]</code>: Principiante</li>
     *       <li><code>filtro[1]</code>: Intermedio</li>
     *       <li><code>filtro[2]</code>: Avanzado</li>
     *       <li><code>filtro[3]</code>: Puntuación media descendente</li>
     *     </ul>
     *   </li>
     *   <li>Devuelve los resultados según los filtros seleccionados.</li>
     *   <li>Incluye carga anticipada de reseñas y etiquetas mediante <code>@EntityGraph</code>.</li>
     * </ul>
     *
     * @param filtros <b>(String)</b> Cadena con 4 valores booleanos separados por comas que indican los filtros aplicados.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de <code>Patron</code> filtrados correctamente.</li>
     *   <li><code>400 BAD REQUEST</code> si la cadena de filtros no tiene exactamente 4 valores.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error durante la ejecución.</li>
     * </ul>
     */
    @GetMapping("/getAllFiltros")
    @EntityGraph(attributePaths = {"reviews", "tags"})
    public ResponseEntity<?> getAllFiltros(
            @RequestParam String filtros
    ) {
        System.out.println("En controlador getAllFiltros");
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

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
    public ResponseEntity<?> getAllBusqueda(
            @RequestParam String query
    ) {
        System.out.println("En controlador getAllBusqueda");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
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
    public ResponseEntity<?> buscarConFiltros(
            @RequestParam String query,
            @RequestParam String filtros
    ) {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
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

    /**
     * Cambia el estado de publicación de un patrón entre <code>Publicado</code> y <code>Borrador</code>,
     * considerando si el patrón ya ha sido adquirido o iniciado por usuarios.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Obtiene el patrón por su ID.</li>
     *   <li>Evalúa si puede cambiar su estado sin conflicto (por ejemplo, si tiene usuarios asociados).</li>
     *   <li>Alterna entre <code>Publicado</code> y <code>Borrador</code> si corresponde.</li>
     *   <li>Fuerza la carga del creador del patrón antes de devolverlo.</li>
     * </ul>
     *
     * @param id <b>(long)</b> ID del patrón a modificar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con el patrón actualizado.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>409 CONFLICT</code> si el patrón está publicado y ya tiene usuarios asociados.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error inesperado.</li>
     * </ul>
     */
    @PostMapping("/estado-publicacion")
    public ResponseEntity<?> cambiarEstadoPublicacion(@RequestParam long id) {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

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

        } catch (Exception e) {
            System.out.println("Error al cambiar el estado del patron");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cambia el estado de un patrón a <code>Inactivo</code>.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Modifica el estado del patrón especificado a <code>Inactivo</code> usando el servicio correspondiente.</li>
     * </ul>
     *
     * @param id <b>(Long)</b> ID del patrón que se desea marcar como inactivo.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con un mensaje de confirmación si el cambio fue exitoso.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @PostMapping("/estado-inactivo")
    public ResponseEntity<?> cambiarEstadoInactivo(@RequestParam Long id) {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }
        servicioPatron.cambiarEstadoPatron(id, Patron.Estado.Inactivo);
        return ResponseEntity.ok("Estado cambiado a inactivo");
    }

    /**
     * Elimina un patrón del sistema si no ha sido adquirido ni iniciado por ningún usuario.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Obtiene el patrón por su ID.</li>
     *   <li>Verifica si el patrón está en uso por otros usuarios.</li>
     *   <li>Si no está en uso, registra una notificación y elimina el patrón.</li>
     * </ul>
     *
     * @param id <b>(long)</b> ID del patrón a eliminar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con mensaje de confirmación si el patrón fue eliminado.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>404 NOT FOUND</code> si no se encuentra el patrón con el ID dado.</li>
     *   <li><code>409 CONFLICT</code> si el patrón ya tiene usuarios que lo han usado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error inesperado.</li>
     * </ul>
     */
    @PostMapping("/eliminar")
    public ResponseEntity<?> eliminarPatron(@RequestParam long id) {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

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

    /**
     * Obtiene todos los patrones publicados creados por el usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recupera todos los patrones con estado <code>Publicado</code> creados por dicho usuario.</li>
     *   <li>Incluye las reseñas asociadas mediante <code>@EntityGraph</code>.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones publicados si la operación fue exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error inesperado.</li>
     * </ul>
     */
    @GetMapping("/patrones-tienda-publicados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesUsuarioPublicados() {
        System.out.println("En controlador obtenerPatronesUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Obtiene todos los patrones en estado <code>Borrador</code> creados por el usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recupera todos los patrones cuyo estado es <code>Borrador</code> y que pertenecen al usuario.</li>
     *   <li>Incluye las reseñas asociadas mediante <code>@EntityGraph</code>.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones si se recuperaron correctamente.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error durante la obtención de los patrones.</li>
     * </ul>
     */
    @GetMapping("/patrones-tienda-borradores")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesUsuarioBorradores() {
        System.out.println("En controlador obtenerPatronesUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Obtiene todos los patrones en estado <code>Inactivo</code> creados por el usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recupera todos los patrones cuyo estado es <code>Inactivo</code> y que pertenecen al usuario.</li>
     *   <li>Incluye las reseñas asociadas mediante <code>@EntityGraph</code>.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones si se recuperaron correctamente.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error durante la obtención de los patrones.</li>
     * </ul>
     */
    @GetMapping("/patrones-tienda-inactivos")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesUsuarioInactivos() {
        System.out.println("En controlador obtenerPatronesUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Obtiene todos los patrones publicados creados por el usuario indicado por el identificador pasado por parámetro.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Recupera todos los patrones con estado <code>Publicado</code> creados por dicho usuario.</li>
     *   <li>Incluye las reseñas asociadas mediante <code>@EntityGraph</code>.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones publicados si la operación fue exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL_SERVER_ERROR</code> si ocurre un error inesperado.</li>
     * </ul>
     */
    @GetMapping("/patrones-tienda-otro")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesOtrosPublicados(@RequestParam Long otroUsuario) {
        System.out.println("En controlador obtenerPatronesUsuario");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

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

    /**
     * Crea un nuevo patrón en estado <code>Borrador</code> para el usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Asigna al usuario autenticado como creador del nuevo patrón.</li>
     *   <li>Establece el estado inicial del patrón como <code>Borrador</code>.</li>
     *   <li>Guarda el patrón en el repositorio.</li>
     * </ul>
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>201 CREATED</code> con el nuevo patrón creado si la operación fue exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @PostMapping("/nuevo")
    public ResponseEntity<?> crearPatron() {
        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario creador = (Usuario) respuestaAutenticacion.getBody();

        Patron patron = new Patron();
        patron.setCreador(creador);
        patron.setEstado(Patron.Estado.Borrador);
        repositorioPatron.save(patron);
        return ResponseEntity.status(HttpStatus.CREATED).body(patron);
    }

    /**
     * Actualiza los datos de un patrón existente con los valores proporcionados por el usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Verifica que el usuario esté autenticado.</li>
     *   <li>Obtiene el patrón a partir del ID proporcionado.</li>
     *   <li>Actualiza todos los campos editables del patrón.</li>
     *   <li>Si se proporciona una lista de imágenes en formato JSON, la interpreta y asigna.</li>
     *   <li>Guarda el patrón actualizado en el repositorio.</li>
     * </ul>
     *
     * @param idPatron ID del patrón a modificar.
     * @param titulo Título del patrón.
     * @param precio Precio del patrón.
     * @param dificultad Nivel de dificultad del patrón.
     * @param descripcion Descripción general del patrón.
     * @param idioma Idioma en el que está escrito el patrón.
     * @param unidad Unidad de medida usada en el patrón.
     * @param lanas Tipos o marcas de lana utilizadas.
     * @param agujaGanchillo Tipo de aguja de ganchillo requerida.
     * @param agujaLanera Tipo de aguja lanera necesaria.
     * @param otros Otros materiales necesarios para completar el patrón.
     * @param abreviaturas Lista de abreviaturas utilizadas en el patrón.
     * @param imagenes (opcional) JSON con las rutas relativas de las imágenes asociadas al patrón.
     * @param tags Lista de etiquetas asociadas al patrón.
     * @param instrucciones Instrucciones detalladas del patrón.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con el patrón actualizado si la operación fue exitosa.</li>
     *   <li><code>400 BAD REQUEST</code> si hubo un error al procesar las imágenes.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     *
     * @throws IOException Si ocurre un error al interpretar el JSON de las imágenes.
     */
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
            @RequestParam(required = false) String imagenes,
            @RequestParam List<String> tags,
            @RequestParam String instrucciones

            ) throws IOException {
        logger.info("Entrando en el método guardarPatron");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Patron patron = (Patron) repositorioPatron.findPatronById(idPatron);

        // Define ruta y nombre para la carpeta de destino de las imagenes de los patrones
        String carpetaDestino = "src/main/resources/static/imagenes/patrones/"; //TODO: eliminar esta linea
        if (imagenes != null && !imagenes.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<String> rutasRelativas = mapper.readValue(imagenes, new TypeReference<List<String>>() {});
                patron.setImagenes(rutasRelativas);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al procesar URLs de imágenes.");
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

    /**
     * Verifica si un patrón específico está guardado en la lista de patrones guardados del usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Autentica al usuario que hace la petición.</li>
     *   <li>Busca el patrón con el ID proporcionado.</li>
     *   <li>Comprueba si ese patrón está presente en la lista de patrones guardados del usuario.</li>
     * </ul>
     *
     * @param idPatron ID del patrón que se desea verificar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con <code>true</code> si el patrón está guardado.</li>
     *   <li><code>200 OK</code> con <code>false</code> si no lo está.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @GetMapping("/estaGuardo")
    public ResponseEntity<?> estaGuardo(@RequestParam Long idPatron) {
        logger.info("Entrando en el método estaGuardado");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Añade o elimina un patrón de la biblioteca de patrones guardados del usuario autenticado.
     * <br><br>
     * Este método actúa como un <strong>toggle</strong>:
     * <ul>
     *   <li>Si el patrón no está guardado, lo añade a la lista de patrones guardados del usuario.</li>
     *   <li>Si el patrón ya está guardado, lo elimina de esa lista.</li>
     * </ul>
     *
     * @param idPatron ID del patrón que se desea guardar o quitar de la biblioteca del usuario.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> si la operación se realiza correctamente.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @PostMapping("/guardarBiblioteca")
    public ResponseEntity<?> guardarBiblioteca(@RequestParam Long idPatron) {
        logger.info("Entrando en el método guardar");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Obtiene todos los patrones que el usuario autenticado ha guardado en su biblioteca personal.
     * <br><br>
     * La lista de patrones guardados se carga directamente desde el objeto Usuario autenticado.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones guardados si la operación es exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL SERVER ERROR</code> si ocurre un error al recuperar los datos.</li>
     * </ul>
     */
    @GetMapping("/patrones-biblioteca-guardados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesBibliotecaGuardados() {
        System.out.println("En controlador obtenerPatronesBibliotecaGuardados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Verifica si un patrón específico está guardado en la lista de patrones comprados del usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Autentica al usuario que hace la petición.</li>
     *   <li>Busca el patrón con el ID proporcionado.</li>
     *   <li>Comprueba si ese patrón está presente en la lista de patrones comprados del usuario.</li>
     * </ul>
     *
     * @param idPatron ID del patrón que se desea verificar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con <code>true</code> si el patrón está comprado.</li>
     *   <li><code>200 OK</code> con <code>false</code> si no lo está.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @GetMapping("/estaComprado")
    public ResponseEntity<?> estaComprado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método estaComprado");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

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

    /**
     * Añade un patrón de la biblioteca de patrones comprados del usuario autenticado.
     * <br><br>
     *
     * @param idPatron ID del patrón que se desea comprar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> si la operación se realiza correctamente.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @PostMapping("/guardarComprado")
    public ResponseEntity<?> guardarComprado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método guardarComprado");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            System.out.println("Patron no encontrado");
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();
        System.out.println("Patron encontrado: " + patron);

        // Guardamos el paton en la lista de patrones comprados
        usuario.getPatronesComprados().add(patron);
        System.out.println("Patrones guardados tras añadir: " + usuario.getPatronesComprados());

        repositorioUsuario.save(usuario);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene todos los patrones que el usuario autenticado ha comprado.
     * <br><br>
     * La lista de patrones guardados se carga directamente desde el objeto Usuario autenticado.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones comprados si la operación es exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL SERVER ERROR</code> si ocurre un error al recuperar los datos.</li>
     * </ul>
     */
    @GetMapping("/patrones-biblioteca-comprados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesBibliotecaComprados() {
        System.out.println("En controlador obtenerPatronesBibliotecaComprados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        try {
            List<Patron> patrones = usuario.getPatronesComprados();
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones comprados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Verifica si un patrón específico está guardado en la lista de patrones empezados del usuario autenticado.
     * <br><br>
     * Este método:
     * <ul>
     *   <li>Autentica al usuario que hace la petición.</li>
     *   <li>Busca el patrón con el ID proporcionado.</li>
     *   <li>Comprueba si ese patrón está presente en la lista de patrones empezados del usuario.</li>
     * </ul>
     *
     * @param idPatron ID del patrón que se desea verificar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con <code>true</code> si el patrón está empezado.</li>
     *   <li><code>200 OK</code> con <code>false</code> si no lo está.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @GetMapping("/estaEmpezado")
    public ResponseEntity<?> estaEmpezado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método estaEmpezado");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();

        if (usuario.getPatronesEmpezados().contains(patron)) {
            return ResponseEntity.ok(true);
        } else{
            return ResponseEntity.ok(false);
        }
    }

    /**
     * Añade un patrón de la biblioteca de patrones empezados del usuario autenticado.
     * <br>
     *
     * @param idPatron ID del patrón que se desea empezar.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> si la operación se realiza correctamente.</li>
     *   <li><code>404 NOT FOUND</code> si el patrón no existe.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     * </ul>
     */
    @PostMapping("/guardarEmpezado")
    public ResponseEntity<?> guardarEmpezado(@RequestParam Long idPatron) {
        logger.info("Entrando en el método guardarEmpezado");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        Optional<Patron> patronOpt = repositorioPatron.findById(idPatron);

        if (patronOpt.isEmpty()) {
            System.out.println("Patron no encontrado");
            return ResponseEntity.notFound().build();
        }
        Patron patron = patronOpt.get();
        System.out.println("Patron encontrado: " + patron);

        // Guardamos el paton en la lista de patrones comprados
        usuario.getPatronesEmpezados().add(patron);
        System.out.println("Patrones guardados tras añadir: " + usuario.getPatronesEmpezados());

        repositorioUsuario.save(usuario);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene todos los patrones que el usuario autenticado ha empezado.
     * <br><br>
     * La lista de patrones guardados se carga directamente desde el objeto Usuario autenticado.
     *
     * @return ResponseEntity<?>
     * <ul>
     *   <li><code>200 OK</code> con la lista de patrones empezados si la operación es exitosa.</li>
     *   <li><code>401 UNAUTHORIZED</code> si el usuario no está autenticado.</li>
     *   <li><code>500 INTERNAL SERVER ERROR</code> si ocurre un error al recuperar los datos.</li>
     * </ul>
     */
    @GetMapping("/patrones-biblioteca-empezados")
    @EntityGraph(attributePaths = {"reviews"})
    public ResponseEntity<?> obtenerPatronesBibliotecaEmpezados() {
        System.out.println("En controlador obtenerPatronesBibliotecaEmpezados");

        ResponseEntity<?> respuestaAutenticacion = autenticacion.autenticar();
        if (!respuestaAutenticacion.getStatusCode().is2xxSuccessful()) {
            return respuestaAutenticacion;
        }

        Usuario usuario = (Usuario) respuestaAutenticacion.getBody();

        try {
            List<Patron> patrones = usuario.getPatronesEmpezados();
            System.out.println("Patrones en lista");
            if (patrones == null || patrones.isEmpty()) {
                patrones = Collections.emptyList();
            }
            return ResponseEntity.ok(patrones);
        } catch (Exception e) {
            System.out.println("Error al conseguir los patrones empezados");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
