package com.tfg.tfgwebapp.clasesDAO;

import jakarta.persistence.*;

import javax.swing.*;

@Entity
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResena;

    @ManyToOne
    @JoinColumn(name = "idPatron") //clave foranea
    private Patron patron;

    @ManyToOne
    @JoinColumn(name = "idUsuario") //clave foranea
    private Usuario usuario;

    private String imagen;
    private int puntuacion;
    private String mensaje;

    // getters y setters
}
