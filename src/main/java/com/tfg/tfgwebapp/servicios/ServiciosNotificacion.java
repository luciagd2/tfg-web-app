package com.tfg.tfgwebapp.servicios;

public class ServiciosNotificacion {

    /*
    private final NotificacionRepository notificacionRepository;
    private final ServicioPlantillaNotificacion templateService;
    private final RepositorioUsuario usuarioRepository;

    public void crearNotificacion(Long idUsuarioDestino, TipoNotificacion tipo, Map<String, String> datos, 
                                  Long idPatronRelacionado, Long idUsuarioRelacionado) {
        Usuario usuario = usuarioRepository.findById(idUsuarioDestino)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String mensaje = templateService.construirMensaje(tipo, datos);

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);
        notificacion.setIdPatronRelacionado(idPatronRelacionado);
        notificacion.setIdUsuarioRelacionado(idUsuarioRelacionado);

        notificacionRepository.save(notificacion);
    }
    */
}
