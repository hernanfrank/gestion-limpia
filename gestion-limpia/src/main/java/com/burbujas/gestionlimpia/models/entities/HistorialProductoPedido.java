package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_producto_pedido")
public class HistorialProductoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Pedido pedido;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEmpty
    @Column(name = "fecha_hora_uso")
    private Timestamp fechaHoraUso;

    @PrePersist
    public void prePersist(){
        if(this.fechaHoraUso == null) {
            this.fechaHoraUso = new Timestamp(System.currentTimeMillis());
        }
    }

    public HistorialProductoPedido(){}

    public HistorialProductoPedido(Producto producto, Pedido pedido, Timestamp fechaHoraUso) {
        this.producto = producto;
        this.pedido = pedido;
        this.fechaHoraUso = fechaHoraUso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Timestamp getFechaHoraUso() {
        return fechaHoraUso;
    }

    public void setFechaHoraUso(Timestamp fechaHoraUso) {
        this.fechaHoraUso = fechaHoraUso;
    }
}
