package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor

public enum TipoMaquina {
    NINGUNO("Ninguno"),// para cancelados, finalizados y pendientes
    LAVADORA("Lavadora"),
    SECADORA("Secadora");
    private final String displayValue;

    public String getDisplayValue() {
        return displayValue;
    }

}
