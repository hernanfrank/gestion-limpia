package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;
import java.time.Period;
import java.util.List;

@Entity
@Table(name = "tipo_pedido")
public class TipoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "La descripcion del tipo de pedido no puede estar vacía")
    private String descripcion;

    @NotEmpty(message = "El precio del tipo de pedido no puede estar vacía")
    private Double precio;

    @NotEmpty(message = "El duración estimada del tipo de pedido no puede estar vacía")
    private Period duracionEstimada;

    @OneToMany(fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Period getDuracionEstimada() {
        return duracionEstimada;
    }

    public void setDuracionEstimada(Period duracion) {
        this.duracionEstimada = duracion;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
