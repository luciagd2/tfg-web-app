package com.tfg.tfgwebapp.clasesModelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
  public class Notificacion {

    @Id
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne
    @Getter @Setter
    @JoinColumn(name = "idUsuario") //clave foranea, el usuario que recive la notificacion
    private Usuario usuario;

    @Getter @Setter
    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;

    //@Getter @Setter
    //private String mensaje;

    // campos adicionales según tipo
    @Getter @Setter
    private Long idPatronRelacionado;

    @Getter @Setter
    private Long idUsuarioRelacionado;

    //Enumerates
    public enum TipoNotificacion {
        GUARDADO,
        COMPRA_EXITOSA,
        PATRON_GRATIS,
        CAMBIO_PRECIO,
        NUEVO_PATRON_CREADOR,
        ACTUALIZADO_GUARDADO,
        ACTUALIZADO_COMPRADO,
        ELIMINADO_GUARDADO,
        COMPRA_AL_CREADOR,
        GUARDADO_AL_CREADOR,
        CALIFICACION,
        NUEVO_SEGUIDOR
    }

    // getters y setters
}
