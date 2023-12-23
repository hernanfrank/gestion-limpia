package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maquinas")
public class Maquina implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de máquina no puede ser vacío")
    private String tipo;

    @NotEmpty(message = "El número de máquina no puede ser vacío")
    private Integer numero;

    @OneToMany(mappedBy = "maquina", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

    public Maquina() {
    }

    public Maquina(String tipo, Integer numero) {
        this.tipo = tipo;
        this.numero = numero;
        this.historialMaquinaPedido = new ArrayList<HistorialMaquinaPedido>();
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<HistorialMaquinaPedido> getHistorialMaquinaPedido() {
        return historialMaquinaPedido;
    }

    public void setHistorialMaquinaPedido(List<HistorialMaquinaPedido> historialMaquinaPedido) {
        this.historialMaquinaPedido = historialMaquinaPedido;
    }
}
