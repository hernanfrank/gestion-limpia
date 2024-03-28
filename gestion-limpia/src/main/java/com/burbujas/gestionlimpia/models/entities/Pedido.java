package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.Prioridad;
import com.burbujas.gestionlimpia.models.entities.enums.TipoPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter @Setter @AllArgsConstructor
public class Pedido implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    @Column(name = "fecha_hora_ingreso")
    private Timestamp fechaHoraIngreso;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "fecha_hora_entrega")
    private Timestamp fechaHoraEntrega;

    @NotNull(message = "El tipo no puede estar vacío")
    @Enumerated(EnumType.STRING)
    private TipoPedido tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El cliente asociado al pedido no puede estar vacío")
    private Cliente cliente;

    @NotNull(message = "El precio no puede estar vacío")
    private Double precio;

    @NotNull(message = "La prioridad no puede estar vacía")
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @NotNull(message = "El estado no puede estar vacío")
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoActual;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialEstadoPedido> historialEstadoPedido;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedido;

    public Pedido() {
        this.fechaHoraIngreso = new Timestamp(System.currentTimeMillis());
        this.prioridad = Prioridad.NORMAL;
        this.estadoActual = EstadoPedido.PENDIENTE;
        this.historialEstadoPedido = new ArrayList<>();
    }

}
