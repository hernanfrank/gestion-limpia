package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
@SQLDelete(sql = "UPDATE productos SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado <> true")
@Getter @Setter @AllArgsConstructor @ToString
public class Producto implements Serializable {

    @Id
    @ToString.Exclude
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de producto no puede estar vacío")
    private String tipo;

    @NotNull(message = "El nivel del producto no puede estar vacío")
    private Double cantidadActual;

    private Double umbralAvisoReabastecimiento;

    @ToString.Exclude
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

    @ToString.Exclude
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedidos;

    @ToString.Exclude
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TipoPedidoProductoMapping> tipoPedidoProductoMapping;

    private boolean eliminado = false;

    public Producto() {
        this.cantidadActual = 0.d;
        this.umbralAvisoReabastecimiento = 5.d;
        this.tipoPedidoProductoMapping = new ArrayList<>();
    }

}
