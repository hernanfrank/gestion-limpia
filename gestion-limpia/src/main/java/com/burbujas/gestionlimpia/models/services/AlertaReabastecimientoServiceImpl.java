package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.controllers.NotificacionController;
import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import com.burbujas.gestionlimpia.models.repositories.IProductoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Service("AlertaReabastecimientoServiceImpl")
public class AlertaReabastecimientoServiceImpl implements IAlertaReabastecimientoService {

    private final IProductoRepository productoRepository;

    private final IConfigRepository configRepository;

    private ScheduledFuture<?> scheduledTask;
    private final ScheduledExecutorService scheduler;

    private SseEmitter sseEmitterBajoStock;

    @Autowired
    public AlertaReabastecimientoServiceImpl(IProductoRepository productoRepository, IConfigRepository configRepository) {
        this.productoRepository = productoRepository;
        this.configRepository = configRepository;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.sseEmitterBajoStock = null;
    }

    @PostConstruct
    public void init() {
        // para que se llame automáticamente en el post construct...
        this.programarAlerta();
    }

    @Override
    // método para actualizar el intervalo cuando se cambie en la configuración
    public void programarAlerta() {
        try{
            if (scheduledTask != null) {
                scheduledTask.cancel(false); // si existe tarea anterior, se cancela
            }

            // busco el intervalo entre alertas desde la configuración
            Config config = configRepository.findById(1L).orElseThrow();
            if(config.getTimeoutAlertaRabastecimiento() != null) {
                Integer intervaloEnMinutos = config.getTimeoutAlertaRabastecimiento();

                // creo la task
                scheduledTask = scheduler.scheduleAtFixedRate(
                        this::verificarNivelesProductos, // tarea a ejecutar
                        0,
                        intervaloEnMinutos, // intervalo entre alertas
                        TimeUnit.MINUTES
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Excepción capturada al programar alerta de reabastecimiento: "+e);
        }
    }

    private void verificarNivelesProductos() {
        List<Producto> productosParaReabastecer = productoRepository.findAll().stream()
                .filter(p -> p.getCantidadActual() <= p.getUmbralAvisoReabastecimiento())
                .toList();
        // si algún producto está por debajo del umbral, se notifica al usuario
        if (!productosParaReabastecer.isEmpty()) {
            this.enviarNotificacion(productosParaReabastecer, false);
        }
    }

    @Override
    // establece la conexión para envío de notificaciones de bajo stock
    public SseEmitter initNotificaciones(){
        // crear un nuevo SseEmitter
        try {
            this.sseEmitterBajoStock = new SseEmitter(0L);

            // manejar desconexiones
            this.sseEmitterBajoStock.onCompletion(() -> this.sseEmitterBajoStock = null);
            this.sseEmitterBajoStock.onTimeout(() -> this.sseEmitterBajoStock = null);
            this.sseEmitterBajoStock.onError((ex) -> this.sseEmitterBajoStock = null);

            this.sseEmitterBajoStock.send(SseEmitter.event().name("INIT").data("(Server) Conexión establecida."));
        } catch (Exception e) {
            System.out.println("AlertaReabastecimientoServiceImpl: Error al intentar crear sseEmitterBajoStock.");
            this.sseEmitterBajoStock = null;
        }
        return this.sseEmitterBajoStock;
    }

    private void enviarNotificacion(List<Producto> productosAReabastecer, Boolean reintento) {
       try{
           if(this.sseEmitterBajoStock != null){
                StringBuilder mensaje = new StringBuilder("ALERTA DE BAJO STOCK<br>");
                productosAReabastecer.forEach(producto -> {
                    mensaje.append("• Producto: ")
                    .append(producto.getTipo())
                    .append(", cantidad actual: ")
                    .append(producto.getCantidadActual())
                    .append("l.<br>");
                });
                this.sseEmitterBajoStock.send(SseEmitter.event().name("NOTIFICACION").data(mensaje));
           }else if (!reintento){
               // si no existe sseEmitter por algún error, lo instancio y pruebo de nuevo
               this.initNotificaciones();
               this.enviarNotificacion(productosAReabastecer, true);
           }else{
               throw new Exception("No se pueden enviar mensajes porque no existe SseEmitter.");
           }
       } catch (Exception e) {
           System.out.println("AlertaReabastecimientoServiceImpl: Error al intentar enviar mensaje por sseEmitterBajoStock. "+e);
       }
    }

    @PreDestroy
    public void autoCleanup(){
        this.cancelarYEliminar();
    }

    @Override
    public void cancelarYEliminar() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdown();
    }
}