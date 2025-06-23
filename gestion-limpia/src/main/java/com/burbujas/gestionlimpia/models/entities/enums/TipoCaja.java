package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoCaja {
    EFECTIVO("Efectivo"),
    BANCO("Banco");

    private final String displayValue;

}
