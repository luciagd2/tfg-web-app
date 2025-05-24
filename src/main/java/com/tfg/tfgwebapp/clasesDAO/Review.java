package com.tfg.tfgwebapp.clasesDAO;

import jakarta.persistence.*;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReview;

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
