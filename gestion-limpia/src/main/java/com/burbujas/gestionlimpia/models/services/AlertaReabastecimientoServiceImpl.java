package com.burbujas.gestionlimpia.models.services;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.repositories.IConfigRepository;
import com.burbujas.gestionlimpia.models.repositories.IProductoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Autowired
    public AlertaReabastecimientoServiceImpl(IProductoRepository productoRepository, IConfigRepository configRepository) {
        this.productoRepository = productoRepository;
        this.configRepository = configRepository;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        programarAlerta();
    }

    private void programarAlerta() {
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
            notificarReabastecimiento(productosParaReabastecer);
        }
    }

    private void notificarReabastecimiento(List<Producto> productosParaReabastecer) {
        System.out.println("AVISO QUEDA POCO STOCK DE LOS SIGUIENTES PRODUCTOS!!!\n");
        productosParaReabastecer.forEach(producto -> {
            System.out.println(producto.getTipo() +", cantidad actual: "+ producto.getCantidadActual()+"l . \n");
        });
    }

    // método para actualizar el intervalo cuando se cambie en la configuración
    public void actualizarIntervalo() {
        programarAlerta();
    }

    @PreDestroy
    public void autoCleanup(){
        cancelarYEliminar();
    }

    public void cancelarYEliminar() {
        if (scheduledTask != null) {
            scheduledTask.cancel(true);
        }
        scheduler.shutdown();
    }
}