package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_estado_pedido")
public class HistorialEstadoPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotEmpty
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotEmpty
    private EstadoPedido estado;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotEmpty
    @Column(name = "fecha_hora_cambio_estado")
    private Timestamp fechaHoraCambioEstado;


    @PrePersist
    public void prePersist() {
        // asigno la fecha y hora del cambio de estado
        this.fechaHoraCambioEstado = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Timestamp getFechaHoraCambioEstado() {
        return fechaHoraCambioEstado;
    }

    public void setFechaHoraCambioEstado(Timestamp fechaHoraCambioEstado) {
        this.fechaHoraCambioEstado = fechaHoraCambioEstado;
    }
}
