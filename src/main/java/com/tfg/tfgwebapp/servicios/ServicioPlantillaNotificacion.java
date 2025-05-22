@Service
public class ServicioPlantillaNotificacion {

    private final Map<TipoNotificacion, String> templates = Map.of(
        TipoNotificacion.GUARDADO, "Has guardado el patrón <strong>{patron}</strong> correctamente.",
        TipoNotificacion.COMPRA_EXITOSA, "Tu compra del patrón <strong>{patron}</strong> fue exitosa.",
        TipoNotificacion.PATRON_GRATIS, "El patrón <strong>{patron}</strong> que tenías en favoritos ahora es gratis.",
        TipoNotificacion.CAMBIO_PRECIO, "El patrón <strong>{patron}</strong> que tenías en favoritos ha cambiado de precio.",
        TipoNotificacion.NUEVO_PATRON_CREADOR, "<strong>{creador}</strong> ha publicado un nuevo patrón.",
        TipoNotificacion.ACTUALIZADO_FAVORITO, "El patrón <strong>{patron}</strong> que tienes guardado ha sido actualizado por el creador.",
        TipoNotificacion.ACTUALIZADO_COMPRADO, "El patrón <strong>{patron}</strong> que tienes comprado ha sido actualizado por el creador.",
        TipoNotificacion.COMPRA_AL_CREADOR, "<strong>{usuario}</strong> ha comprado tu patrón <strong>{patron}</strong>.",
        TipoNotificacion.GUARDADO_AL_CREADOR, "<strong>{usuario}</strong> ha guardado tu patrón <strong>{patron}</strong>.",
        TipoNotificacion.GUSTADO_AL_CREADOR, "A <strong>{usuario}</strong> le ha gustado tu patrón <strong>{patron}</strong>.",
        TipoNotificacion.CALIFICACION, "Tu patrón <strong>{patron}</strong> ha sido calificado con <strong>{estrellas} estrellas</strong> por <strong>{usuario}</strong>.",
        TipoNotificacion.TENDENCIA, "Tu patrón <strong>{patron}</strong> es tendencia."
    );

    public String construirMensaje(TipoNotificacion tipo, Map<String, String> datos) {
        String template = templates.get(tipo);
        if (template == null) {
            throw new IllegalArgumentException("Tipo de notificación no soportado: " + tipo);
        }

        for (Map.Entry<String, String> entry : datos.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }
}
