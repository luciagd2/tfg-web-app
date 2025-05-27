package com.tfg.tfgwebapp.clasesModelo;

import java.util.List;

public class Instruccion {
    private String titulo;
    private List<Contenido> contenido;

    public static class Contenido {
        private String tipo;  // "vuelta", "subtitulo", "info", "imagen"
        private String texto; // o "contenido" si usas esa clave

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getTexto() {
            return texto;
        }

        public void setTexto(String texto) {
            this.texto = texto;
        }
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Contenido> getContenido() {
        return contenido;
    }

    public void setContenido(List<Contenido> contenido) {
        this.contenido = contenido;
    }
}