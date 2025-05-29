package com.tfg.tfgwebapp.clasesModelo;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String nombreUsuario;
    private String descripcionUsuario;
    private boolean esCreador;
    @Column(length = 500)
    private String imagenPerfil;

    // Usuarios a los que este usuario sigue
    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(
            name = "usuario_seguidores",
            joinColumns = @JoinColumn(name = "usaurio_id")
    )
    @Column(name = "seguidor")
    private List<Long> idsSeguidores = new ArrayList<>();

    /*
    // Usuarios a los que este usuario sigue
    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "usuario_seguidos",
            joinColumns = @JoinColumn(name = "seguidor_id"),
            inverseJoinColumns = @JoinColumn(name = "seguido_id")
    )
    private List<Usuario> seguidos = new ArrayList<>();

    // Usuarios que siguen a este usuario (mapeado inversamente)
    @Getter
    @Setter
    @JsonBackReference
    @ManyToMany(mappedBy = "seguidos")
    private List<Usuario> seguidores = new ArrayList<>();
*/

    // Patrones empezados
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_empezados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesEmpezados = new ArrayList<>();

    // Patrones guardados
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_guardados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesGuardados = new ArrayList<>();

    // Patrones comprados
    @ManyToMany
    @JoinTable(
            name = "usuario_patrones_comprados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "patron_id")
    )
    private List<Patron> patronesComprados = new ArrayList<>();

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDescripcionUsuario() {
        return descripcionUsuario;
    }

    public void setDescripcionUsuario(String descripcionUsuario) {
        this.descripcionUsuario = descripcionUsuario;
    }

    public boolean isEsCreador() { return esCreador;}

    public void setEsCreador(boolean esCreador) { this.esCreador = esCreador;}

    public String getImagenPerfil() { return imagenPerfil;}

    public void setImagenPerfil(String imagenPerfil) { this.imagenPerfil = imagenPerfil;}
}

