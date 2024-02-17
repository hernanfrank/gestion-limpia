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
import java.util.List;

@Entity
@Table(name = "producto")
@Getter @Setter @AllArgsConstructor
public class Producto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El tipo de producto no puede estar vacío")
    private String tipo;

    @NotNull(message = "El nivel del producto no puede estar vacío")
    private Double cantidadActual;

    @NotNull(message = "La cantidad por unidad no puede estar vacía")
    private Double cantidadPorUnidad;

    @NotNull(message = "La cantidad usada por pedido no puede ser vacía")
    private Double cantidadUsadaPorPedido;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Reabastecimiento> reabastecimientos;

    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<HistorialProductoPedido> historialProductoPedidos;

    public Producto() {
        this.cantidadActual = 0.d;
        this.cantidadPorUnidad = 10.d;
    }

}
