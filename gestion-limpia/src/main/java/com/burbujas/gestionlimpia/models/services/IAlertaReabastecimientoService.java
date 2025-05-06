package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Producto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

public interface IAlertaReabastecimientoService {

    public void programarAlerta();

    public SseEmitter initNotificaciones();

    public void cancelarYEliminar();

}
