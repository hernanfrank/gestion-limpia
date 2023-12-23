package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de producto no puede estar vacío")
    private String tipo;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedidos;

    public Producto() {
        this.reabastecimientos = new ArrayList<Reabastecimiento>();
        this.historialProductoPedidos = new ArrayList<HistorialProductoPedido>();
    }

    public Producto(String tipo, List<Reabastecimiento> reabastecimientos, List<HistorialProductoPedido> historialProductoPedidos) {
        this.tipo = tipo;
        this.reabastecimientos = reabastecimientos;
        this.historialProductoPedidos = historialProductoPedidos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<Reabastecimiento> getReabastecimientos() {
        return reabastecimientos;
    }

    public void setReabastecimientos(List<Reabastecimiento> reabastecimientos) {
        this.reabastecimientos = reabastecimientos;
    }

    public List<HistorialProductoPedido> getHistorialProductoPedidos() {
        return historialProductoPedidos;
    }

    public void setHistorialProductoPedidos(List<HistorialProductoPedido> historialProductoPedidos) {
        this.historialProductoPedidos = historialProductoPedidos;
    }
}
