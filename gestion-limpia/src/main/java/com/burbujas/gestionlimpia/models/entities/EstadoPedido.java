package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estados_pedidos")
public class EstadoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La descripción del estado no puede estar vacía")
    private String descripcion;

    @OneToMany(mappedBy = "estado", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialEstadoPedido> historialEstados;

    public EstadoPedido(){
        this.historialEstados = new ArrayList<HistorialEstadoPedido>();
    }

    public EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
        this.historialEstados = new ArrayList<HistorialEstadoPedido>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<HistorialEstadoPedido> getHistorialEstados() {
        return historialEstados;
    }

    public void setHistorialEstados(List<HistorialEstadoPedido> historialEstados) {
        this.historialEstados = historialEstados;
    }
}
