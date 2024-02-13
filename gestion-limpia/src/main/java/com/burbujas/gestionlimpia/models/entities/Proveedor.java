package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre del proveedor no puede estar vacío")
    private String nombre;

    @OneToMany(mappedBy = "proveedor", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

    public Proveedor() {
    }

    public Proveedor(Long id, String nombre, List<Reabastecimiento> reabastecimientos) {
        this.id = id;
        this.nombre = nombre;
        this.reabastecimientos = reabastecimientos;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Reabastecimiento> getReabastecimientos() {
        return reabastecimientos;
    }

    public void setReabastecimientos(List<Reabastecimiento> reabastecimientos) {
        this.reabastecimientos = reabastecimientos;
    }
}
