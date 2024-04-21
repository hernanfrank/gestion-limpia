package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "historial_maquinas_pedidos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HistorialMaquinaPedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    private Maquina maquinaAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Maquina maquinaNueva;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Column(name = "fecha_hora_asignacion")
    private Timestamp fechaHoraAsignacion;

    public HistorialMaquinaPedido(Pedido pedido, Maquina maquinaAnterior, Maquina maquinaNueva, Timestamp fechaHoraAsignacion) {
        this.pedido = pedido;
        this.maquinaAnterior = maquinaAnterior;
        this.maquinaNueva = maquinaNueva;
        this.fechaHoraAsignacion = fechaHoraAsignacion;
    }
}
