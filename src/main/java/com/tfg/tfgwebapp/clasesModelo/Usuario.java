package com.tfg.tfgwebapp.clasesModelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter @Setter
    private String email;

    @Getter @Setter
    private String password;

    @Getter @Setter
    private String nombreUsuario;

    @Getter @Setter
    private String descripcionUsuario;

    @Getter @Setter
    private boolean esCreador;

    @Getter @Setter
    @Column(length = 500)
    private String imagenPerfil;

    // Usuarios a los que este usuario sigue
    @Getter @Setter
    @ElementCollection
    @CollectionTable(
            name = "usuario_seguidores",
            joinColumns = @JoinColumn(name = "usaurio_id")
    )
    @Column(name = "seguidor")
    private List<Long> idsSeguidores = new ArrayList<>();

    // Patrones empezados
    @Getter @Setter
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_empezados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesEmpezados = new ArrayList<>();

    // Patrones guardados
    @Getter @Setter
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_guardados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesGuardados = new ArrayList<>();

    // Patrones comprados
    @Getter @Setter
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_comprados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesComprados = new ArrayList<>();
}

