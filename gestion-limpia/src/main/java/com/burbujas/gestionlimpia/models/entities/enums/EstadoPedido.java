package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum EstadoPedido {
    INGRESADO("Ingresado"),
    PENDIENTE("Pendiente"),
    LAVADO("En lavado"),
    SECADO("En secado"),
    FINALIZADO("Finalizado"),
    COBRADO("Cobrado"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado");

    private final String displayValue;


    public int obtenerPrioridad() {
        if (this.equals(EstadoPedido.FINALIZADO)) return 1;
        if (this.equals(EstadoPedido.COBRADO)) return 2;
        if (this.equals(EstadoPedido.PENDIENTE)
                || this.equals(EstadoPedido.LAVADO)
                || this.equals(EstadoPedido.SECADO)) return 3;
        if (this.equals(EstadoPedido.ENTREGADO)) return 4;
        if (this.equals(EstadoPedido.CANCELADO)) return 5;
        return 6; // Para cualquier otro estado no contemplado
    };
}