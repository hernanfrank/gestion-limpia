package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public enum TipoMovimientoCaja {
    INGRESO("Ingreso"),
    EGRESO("Egreso"),
    NOTACREDITO("Nota de crédito"),
    NOTADEBITO("Nota de débito");

    private final String displayValue;

}
