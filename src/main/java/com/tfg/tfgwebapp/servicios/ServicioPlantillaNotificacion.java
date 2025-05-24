package com.tfg.tfgwebapp.servicios;

import com.tfg.tfgwebapp.clasesDAO.Notificacion;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ServicioPlantillaNotificacion {
    /*
    private final Map<Notificacion.TipoNotificacion, String> templates = Map.of(
        Notificacion.TipoNotificacion.GUARDADO, "Has guardado el patrón <strong>{patron}</strong> correctamente.",
        Notificacion.TipoNotificacion.COMPRA_EXITOSA, "Tu compra del patrón <strong>{patron}</strong> fue exitosa.",
        Notificacion.TipoNotificacion.PATRON_GRATIS, "El patrón <strong>{patron}</strong> que tenías en favoritos ahora es gratis.",
        Notificacion.TipoNotificacion.CAMBIO_PRECIO, "El patrón <strong>{patron}</strong> que tenías en favoritos ha cambiado de precio.",
        Notificacion.TipoNotificacion.NUEVO_PATRON_CREADOR, "<strong>{creador}</strong> ha publicado un nuevo patrón.",
        Notificacion.TipoNotificacion.ACTUALIZADO_FAVORITO, "El patrón <strong>{patron}</strong> que tienes guardado ha sido actualizado por el creador.",
        Notificacion.TipoNotificacion.ACTUALIZADO_COMPRADO, "El patrón <strong>{patron}</strong> que tienes comprado ha sido actualizado por el creador.",
        Notificacion.TipoNotificacion.COMPRA_AL_CREADOR, "<strong>{usuario}</strong> ha comprado tu patrón <strong>{patron}</strong>.",
        Notificacion.TipoNotificacion.GUARDADO_AL_CREADOR, "<strong>{usuario}</strong> ha guardado tu patrón <strong>{patron}</strong>.",
        Notificacion.TipoNotificacion.GUSTADO_AL_CREADOR, "A <strong>{usuario}</strong> le ha gustado tu patrón <strong>{patron}</strong>.",
        Notificacion.TipoNotificacion.CALIFICACION, "Tu patrón <strong>{patron}</strong> ha sido calificado con <strong>{estrellas} estrellas</strong> por <strong>{usuario}</strong>.",
        Notificacion.TipoNotificacion.TENDENCIA, "Tu patrón <strong>{patron}</strong> es tendencia."
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
    */
}
