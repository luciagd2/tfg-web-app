package com.tfg.tfgwebapp.modelo;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nombreUsuario;
    private String descripcionUsuario;
    private boolean esCreador;
    @Column(length = 500)
    private String imagenPerfil;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDescripcionUsuario() {
        return descripcionUsuario;
    }

    public void setDescripcionUsuario(String descripcionUsuario) {
        this.descripcionUsuario = descripcionUsuario;
    }

    public boolean isEsCreador() { return esCreador;}

    public void setEsCreador(boolean esCreador) { this.esCreador = esCreador;}

    public String getImagenPerfil() { return imagenPerfil;}

    public void setImagenPerfil(String imagenPerfil) { this.imagenPerfil = imagenPerfil;}
}

