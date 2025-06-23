package com.burbujas.gestionlimpia.models.entities;

import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.entities.enums.Prioridad;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@SQLDelete(sql = "UPDATE pedidos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @AllArgsConstructor @ToString
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

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El tipo no puede estar vacío")
    private TipoPedido tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "El cliente asociado al pedido no puede estar vacío")
    private Cliente cliente;

    @NotNull(message = "El precio no puede estar vacío")
    @DecimalMin(value = "0.0", message = "El precio no puede ser inferior a $0")
    private Double precio;

    @NotNull(message = "La prioridad no puede estar vacía")
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @NotNull(message = "El estado no puede estar vacío")
    @Enumerated(EnumType.STRING)
    private EstadoPedido estadoActual;

    @Size(max = 250, message = "La descripción no puede contener más de 250 caracteres")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    private Maquina maquinaActual;

    @ToString.Exclude
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialEstadoPedido> historialEstadoPedido;

    @ToString.Exclude
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialMaquinaPedido> historialMaquinaPedido;

    @ToString.Exclude
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedido;

    @ToString.Exclude
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MovimientoCaja> movimientosCaja;

    private boolean eliminado = false;

    public Pedido() {
        this.fechaHoraIngreso = new Timestamp(System.currentTimeMillis());
        this.prioridad = Prioridad.NORMAL;
        this.estadoActual = EstadoPedido.PENDIENTE;
        this.maquinaActual = null;
        this.historialEstadoPedido = new ArrayList<>();
        this.historialMaquinaPedido = new ArrayList<>();
        this.movimientosCaja = new ArrayList<>();
    }

}
