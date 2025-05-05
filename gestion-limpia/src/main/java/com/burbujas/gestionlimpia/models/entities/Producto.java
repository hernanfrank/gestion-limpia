package com.burbujas.gestionlimpia.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "productos")
@Getter @Setter @AllArgsConstructor
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de producto no puede estar vacío")
    private String tipo;

    @NotNull(message = "El nivel del producto no puede estar vacío")
    private Double cantidadActual;

    private Double umbralAvisoReabastecimiento;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedidos;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TipoPedidoProductoMapping> tipoPedidoProductoMapping;

    public Producto() {
        this.cantidadActual = 0.d;
        this.umbralAvisoReabastecimiento = 5.d;
        this.tipoPedidoProductoMapping = new ArrayList<>();
    }

}
