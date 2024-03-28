package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public enum TipoPedido {
    ROPA("Ropa"),
    ACOLCHADO("Acolchado"),
    MIXTO("Mixto");

    private final String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }

}
