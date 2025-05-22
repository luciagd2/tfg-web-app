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

    private String tipo;
    private String mensaje;

    // campos adicionales según tipo
    private Long idPatronRelacionado;
    private Long idUsuarioRelacionado;

    // getters y setters
}
