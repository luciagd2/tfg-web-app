package com.tfg.tfgwebapp.clasesDAO;

import jakarta.persistence.*;

import javax.swing.*;

@Entity
public class Patron {
    //Emunerates
    private enum dificultad {Principiante, Intermedio, Avanzado};

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
    private dificultad dificultad;
    private String descripcion;
    private enum idioma { Español, Inglés, Frances, Aleman};
    private enum unidad {Centímetros, Pulgadas}
    
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
