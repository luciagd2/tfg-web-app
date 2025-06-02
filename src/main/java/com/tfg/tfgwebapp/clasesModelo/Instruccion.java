package com.tfg.tfgwebapp.clasesModelo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class Instruccion {

    @Getter @Setter
    private String titulo;

    @Getter @Setter
    private List<Contenido> contenido;

    public static class Contenido {

        @Getter @Setter
        private String tipo;  // "vuelta", "subtitulo", "info", "imagen"

        @Getter @Setter
        private String texto;
    }
}