package com.tfg.tfgwebapp.clasesDAO;

import jakarta.persistence.*;

import javax.swing.*;

@Entity
  public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNotificacion;

    @ManyToOne
    @JoinColumn(name = "idUsuario") //clave foranea
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipo;
    
    private String mensaje;

    // campos adicionales según tipo
    private Long idPatronRelacionado;
    private Long idUsuarioRelacionado;

    //Enumerates
    public enum TipoNotificacion {
        GUARDADO,
        COMPRA_EXITOSA,
        PATRON_GRATIS,
        CAMBIO_PRECIO,
        NUEVO_PATRON_CREADOR,
        ACTUALIZADO_FAVORITO,
        ACTUALIZADO_COMPRADO,
        COMPRA_AL_CREADOR,
        GUARDADO_AL_CREADOR,
        GUSTADO_AL_CREADOR,
        CALIFICACION,
        TENDENCIA
    }

    // getters y setters
}
