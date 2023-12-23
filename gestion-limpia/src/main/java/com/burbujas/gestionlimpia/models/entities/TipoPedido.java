package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

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

    @NotEmpty(message = "La duración estimada del tipo de pedido no puede estar vacía")
    private Integer duracionEstimada; // en dias

    @OneToMany(mappedBy = "tipo", fetch = FetchType.LAZY)
    private List<Pedido> pedidos;

    public TipoPedido() {
    }

    public TipoPedido(String descripcion, Double precio, Integer duracionEstimada) {
        this.descripcion = descripcion;
        this.precio = precio;
        this.duracionEstimada = duracionEstimada;
    }

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

    public Integer getDuracionEstimada() {
        return this.duracionEstimada;    }

    public void setDuracionEstimada(Integer duracion) {
        this.duracionEstimada = duracion;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
