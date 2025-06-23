package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.services.IAlertaReabastecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final IAlertaReabastecimientoService alertaReabastecimientoService;

    @Autowired
    public NotificacionController(IAlertaReabastecimientoService alertaReabastecimientoService) {
        this.alertaReabastecimientoService = alertaReabastecimientoService;
    }

    @GetMapping("/bajoStock")
    public SseEmitter conectarNotificacionesBajoStock() {
        return this.alertaReabastecimientoService.initNotificaciones();
    }

}
