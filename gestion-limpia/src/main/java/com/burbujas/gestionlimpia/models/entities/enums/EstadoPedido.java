package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public enum EstadoPedido {
    INGRESADO("Ingresado"),
    PENDIENTE("Pendiente"),
    LAVADO("En lavado"),
    SECADO("En secado"),
    FINALIZADO("Finalizado"),
    COBRADO("Cobrado"),
    CANCELADO("Cancelado");

    private final String displayValue;


    public String getDisplayValue() {
        return displayValue;
    }
}