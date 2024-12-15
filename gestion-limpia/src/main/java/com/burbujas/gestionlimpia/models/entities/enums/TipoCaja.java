package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TipoCaja {
    EFECTIVO("Efectivo"),
    BANCO("Banco");

    private final String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }
}
