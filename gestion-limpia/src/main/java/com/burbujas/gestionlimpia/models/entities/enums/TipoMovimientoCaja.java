package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public enum TipoMovimientoCaja {
    INGRESO("Ingreso"),
    EGRESO("Egreso"),
    NOTACREDITO("Nota de crédito"),
    NOTADEBITO("Nota de débito");

    private final String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }
}
