package com.burbujas.gestionlimpia.models.services;

public interface IClaveResetService {
    public void solicitarCambioClave(String email);
    public int cambiarClave(String claveActual, String claveNueva);
    public int cambiarClaveConToken(String token, String claveNueva);
}
