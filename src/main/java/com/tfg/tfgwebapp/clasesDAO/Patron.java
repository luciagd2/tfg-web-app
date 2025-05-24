package com.tfg.tfgwebapp.clasesDAO;

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
    @Getter
    private String agujaLanera;
    private String otros;

    //Otros
    private String abreviaturas;

    @ElementCollection
    @CollectionTable(name = "patron_tags", joinColumns = @JoinColumn(name = "patron_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "patron_imagenes", joinColumns = @JoinColumn(name = "patron_id"))
    @Column(name = "imagen")
    private List<String> imagenes = new ArrayList<>();

    @OneToMany(mappedBy = "patron", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    //Instrucciones
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

    public void setAgujadaLanera(String agujaLanera) {
        this.agujaLanera = agujaLanera;
    }

    public String getOtros() {
        return otros;
    }

    public void setOtros(String otros) {
        this.otros = otros;
    }

    public String getAbreviaturas() {
        return abreviaturas;
    }

    public void setAbreviaturas(String abreviaturas) {
        this.abreviaturas = abreviaturas;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }

    public List<Review> getResenas() {
        return reviews;
    }

    public void setResenas(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(String instrucciones) {
        this.instrucciones = instrucciones;
    }
}


