package com.tfg.tfgwebapp.clasesDAO;

import jakarta.persistence.*;

import javax.swing.*;

@Entity
public class Patron {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private double precio;
    private boolean publicado;

    @JoinColumn(name = "idCreador") // FK a Usuario.id
    private Long idCreador;
    
    //Información
    @Enumerated(EnumType.STRING)
    private Dificultad dificultad;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private Idioma idioma;
    @Enumerated(EnumType.STRING)
    private enum Unidad unidad;
    
    //Materiales
    private String lanas;
    private String agujaGanchillo;
    private String agujadaLanera;
    private String otros;
    
    //Otros
    private String abreviaturas;
    
    @OneToMany(mappedBy = "patron", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas = new ArrayList<>();
    //Instrucciones



    /*
    public class Patron {

    @Id
    private String id;

    private String nombre;
    private Long precio;
    private Boolean publicado; // true o false
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Nivel nivel; // PRINCIPIANTE, INTERMEDIO, AVANZADO

    @ManyToOne
    private Usuario creador;

    @Embedded
    private Informacion informacion;

    @ElementCollection
    private Map<String, String> materiales; // Map de materiales: lanas, agujaGanchillo, agujaLanera, otros

    @Enumerated(EnumType.STRING)
    private Abreviaturas abreviatura; // Enum para abreviaturas

    @ElementCollection
    private List<String> tags;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "patron_id")
    private List<Imagen> imagenes;

    @OneToMany(mappedBy = "patron", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Resena> resenas;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "patron_id")
    private List<Instruccion> instrucciones;
}
    */
}

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

