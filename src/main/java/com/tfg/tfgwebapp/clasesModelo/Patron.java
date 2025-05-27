package com.tfg.tfgwebapp.clasesModelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Patron {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    private String titulo;

    @Setter
    @Getter
    private double precio;

    @Setter
    @Getter
    private boolean publicado;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_creador", nullable = false)
    private Usuario creador;

    //Información
    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Dificultad dificultad;

    @Setter
    @Getter
    private String descripcion;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Idioma idioma;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Unidad unidad;

    //Materiales
    @Setter
    @Getter
    private String lanas;

    @Setter
    @Getter
    private String agujaGanchillo;

    @Setter
    @Getter
    private String agujaLanera;

    @Setter
    @Getter
    private String otros;

    //Otros
    @Setter
    @Getter
    private String abreviaturas;

    @Setter
    @Getter
    @ElementCollection
    @CollectionTable(name = "patron_tags", joinColumns = @JoinColumn(name = "patron_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Setter
    @Getter
    @ElementCollection
    @CollectionTable(name = "patron_imagenes", joinColumns = @JoinColumn(name = "patron_id"))
    @Column(name = "imagen")
    private List<String> imagenes = new ArrayList<>();

    @Setter
    @Getter
    @OneToMany(mappedBy = "patron", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    //Instrucciones
    @Setter
    @Getter
    @Column(columnDefinition = "TEXT")
    private String instrucciones;


    //Enumerates
    public enum Dificultad {
        Principiante,
        Intermedio,
        Avanzado
    }

    public enum Idioma {
        Español,
        Inglés,
        Frances,
        Aleman
    }

    public enum Unidad {
        Centímetros,
        Pulgadas
    }
}


