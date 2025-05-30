package com.tfg.tfgwebapp.clasesModelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
public class Compra {
    @Id
    @Setter
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @Setter
    @Getter
    private Usuario usuario;

    @ManyToOne
    @JsonIgnore
    @Setter
    @Getter
    private Patron patron;

    @Setter
    @Getter
    private LocalDateTime fecha;
}
