package com.burbujas.gestionlimpia.models.entities.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Prioridad {
    BAJA("Baja"),
    NORMAL("Normal"),
    PRIORITARIO("Prioritario"),
    URGENTE("Urgente");

    private final String displayValue;


}
