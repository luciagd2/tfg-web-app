package com.tfg.tfgwebapp.clasesModelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Review {
    // Constructor vacío
    public Review() {}

    @Id
    @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReview;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "idPatron") //clave foranea
    @JsonBackReference
    private Patron patron;

    @Getter @Setter
    @ManyToOne
    @JoinColumn(name = "idUsuario") //clave foranea
    private Usuario usuario;

    @Getter @Setter
    private String imagen;

    @Getter @Setter
    private int puntuacion;

    @Getter @Setter
    private String mensaje;
}
