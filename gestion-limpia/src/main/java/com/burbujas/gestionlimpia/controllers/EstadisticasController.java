package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.services.*;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Date;
import java.util.*;


@Controller
@RequestMapping("/estadisticas")
public class EstadisticasController {

    private final ICajaService cajaService;
    private final IPedidoService pedidoService;
    private final IClienteService clienteService;
    private final IProductoService productoService;
    private final IProveedorService proveedorService;

    @Autowired
    public EstadisticasController(ICajaService cajaService, IPedidoService pedidoService, IClienteService clienteService, IProductoService productoService, IProveedorService proveedorService) {
        this.cajaService = cajaService;
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.proveedorService = proveedorService;
    }

    @GetMapping(value = {"","/","/{fechaBusqueda}"})
    public String mostrarEstadisticas(Model model,
                                      @Nullable @PathVariable("fechaBusqueda") String fechaBusqueda
                                      ) {
        model.addAttribute("titulo", "Estadísticas");

        List<Date> mesesConDatos = pedidoService.findAllMonthsWithPedidosEntregados();
        model.addAttribute("mesesConDatos", mesesConDatos);

        if(mesesConDatos.isEmpty()){
            model.addAttribute("sinDatos",true);
            return "estadisticas";
        }else{
            model.addAttribute("sinDatos",false);
        }

        if (fechaBusqueda == null) {
            fechaBusqueda = mesesConDatos.get(0).toString();
        }
        model.addAttribute("fechaBusqueda", fechaBusqueda);

        //RESUMEN
        // ingresos
        double ingresosMesActual = cajaService.sumAllByMes(fechaBusqueda);
        HashMap<String, Number> totalIngresos = new HashMap<>();

        double porcentajeCambioIngresos = 0.0;
        int indexOfProxMes = mesesConDatos.indexOf(Date.valueOf(fechaBusqueda)) + 1;
        if (indexOfProxMes < mesesConDatos.size()) { // previene un overflow
            double ingresosMesAnterior = cajaService.sumAllByMes(mesesConDatos.get(indexOfProxMes).toString());
            porcentajeCambioIngresos = ((ingresosMesActual - ingresosMesAnterior) / ingresosMesAnterior);
        }

        totalIngresos.put("valor", ingresosMesActual);
        totalIngresos.put("porcentaje", porcentajeCambioIngresos);
        model.addAttribute("totalIngresos", totalIngresos);

        // pedidos completados
        int pedidosCompletadosMesActual = pedidoService.countAllInMes(fechaBusqueda);
        HashMap<String, Number> pedidosCompletados = new HashMap<>();

        double porcentajeCambioPedidosCompletados = 0.0;
        if (indexOfProxMes < mesesConDatos.size()) { // previene un overflow
            int pedidosCompletadosMesAnterior = pedidoService.countAllInMes(mesesConDatos.get(indexOfProxMes).toString());
            porcentajeCambioPedidosCompletados = ((double) (pedidosCompletadosMesActual - pedidosCompletadosMesAnterior) / pedidosCompletadosMesAnterior);
        }

        pedidosCompletados.put("valor", pedidosCompletadosMesActual);
        pedidosCompletados.put("porcentaje", porcentajeCambioPedidosCompletados);
        model.addAttribute("pedidosCompletados", pedidosCompletados);

        // clientes
        int cantidadClientesMesActual = clienteService.countAllByMes(fechaBusqueda);
        HashMap<String, Number> cantidadClientes = new HashMap<>();

        double porcentajeCambioCantidadClientes = 0.0;
        if (indexOfProxMes < mesesConDatos.size()) { // previene un overflow
            int cantidadClientesMesAnterior = clienteService.countAllByMes(mesesConDatos.get(indexOfProxMes).toString());
            porcentajeCambioCantidadClientes = ((double) (cantidadClientesMesActual - cantidadClientesMesAnterior) / cantidadClientesMesAnterior);
        }

        cantidadClientes.put("valor", cantidadClientesMesActual);
        cantidadClientes.put("porcentaje", porcentajeCambioCantidadClientes);
        model.addAttribute("cantidadClientes", cantidadClientes);

        // costos inventario
        double costoInventarioMesActual = productoService.sumAllReabastecimientosInMes(fechaBusqueda);
        HashMap<String, Number> costosInventario = new HashMap<>();

        double porcentajeCambioCostoInventario = 0.0;
        if (indexOfProxMes < mesesConDatos.size()) { // previene un overflow
            double costoInventarioMesAnterior = productoService.sumAllReabastecimientosInMes(mesesConDatos.get(indexOfProxMes).toString());
            porcentajeCambioCostoInventario = ((costoInventarioMesActual - costoInventarioMesAnterior) / costoInventarioMesAnterior);
        }

        costosInventario.put("valor", costoInventarioMesActual);
        costosInventario.put("porcentaje", porcentajeCambioCostoInventario);
        model.addAttribute("costosInventario", costosInventario);

        //tiempo por pedido
        double tiempoPorPedidoMesActual = pedidoService.avgTiempoPorPedidoGroupByMes(fechaBusqueda);
        HashMap<String, Number> tiempoPorPedido = new HashMap<>();

        double porcentajeCambiotiempoPorPedido = 0.0;
        if (indexOfProxMes < mesesConDatos.size()) { // previene un overflow
            double tiempoPorPedidoMesAnterior = pedidoService.avgTiempoPorPedidoGroupByMes(mesesConDatos.get(indexOfProxMes).toString());
            porcentajeCambiotiempoPorPedido = ((tiempoPorPedidoMesActual - tiempoPorPedidoMesAnterior) / tiempoPorPedidoMesAnterior);
        }

        tiempoPorPedido.put("valor", tiempoPorPedidoMesActual);
        tiempoPorPedido.put("porcentaje", porcentajeCambiotiempoPorPedido);
        model.addAttribute("tiempoPorPedido", tiempoPorPedido);

        //GRAFICOS
        //Ingresos
        //ingresos por mes
        List<Object[]> ingresosPorMesQuery = cajaService.sumAllGroupByMes(fechaBusqueda);
        StringBuilder ingresosPorMesJsonString = new StringBuilder("[");
        for (Object[] fila : ingresosPorMesQuery) {
            ingresosPorMesJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        ingresosPorMesJsonString.append("]");

        model.addAttribute("ingresosPorMes", ingresosPorMesJsonString);

        //ingresos por día
        List<Object[]> ingresosPorDiaQuery = cajaService.sumAllGroupByDia(fechaBusqueda);
        StringBuilder ingresosPorDiaJsonString = new StringBuilder("[");
        for (Object[] fila : ingresosPorDiaQuery) {
            ingresosPorDiaJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        ingresosPorDiaJsonString.append("]");

        model.addAttribute("ingresosPorDia", ingresosPorDiaJsonString);

        //ingresos por tipo caja
        List<Object[]> ingresosPorTipoCaja = cajaService.sumAllGroupByTipoCaja(fechaBusqueda);
        StringBuilder ingresosPorTipoCajaJsonString = new StringBuilder("[");
        for (Object[] fila : ingresosPorTipoCaja) {
            ingresosPorTipoCajaJsonString.append("{").append("label:'").append(fila[0]).append("',value:").append(fila[1]).append("},");
        }
        ingresosPorTipoCajaJsonString.append("]");

        model.addAttribute("ingresosPorTipoCaja", ingresosPorTipoCajaJsonString);

        //ingresos por tipo de pedido
        List<Object[]> ingresosPorTipoPedido = cajaService.sumAllGroupByTipoPedido(fechaBusqueda);
        StringBuilder ingresosPorTipoPedidoJsonString = new StringBuilder("[");
        for (Object[] fila : ingresosPorTipoPedido) {
            ingresosPorTipoPedidoJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        ingresosPorTipoPedidoJsonString.append("]");

        model.addAttribute("ingresosPorTipoPedido", ingresosPorTipoPedidoJsonString);

        //Clientes
        //clientes por pedidos
        List<Object[]> clientesPorPedidos = pedidoService.countAllGroupByCliente();
        StringBuilder clientesPorPedidosJsonString = new StringBuilder("[");
        for (Object[] fila : clientesPorPedidos) {
            clientesPorPedidosJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        clientesPorPedidosJsonString.append("]");

        model.addAttribute("clientesPorPedidos", clientesPorPedidosJsonString);

        //clientes por ingresos
        List<Object[]> clientesPorIngresos = pedidoService.sumAllGroupByCliente();
        StringBuilder clientesPorIngresosJsonString = new StringBuilder("[");
        for (Object[] fila : clientesPorIngresos) {
            clientesPorIngresosJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        clientesPorIngresosJsonString.append("]");

        model.addAttribute("clientesPorIngresos", clientesPorIngresosJsonString);

        //Pedidos
        //pedidos por estado
        List<Object[]> pedidosPorMes = pedidoService.countAllGroupByMes(fechaBusqueda);
        StringBuilder pedidosPorMesJsonString = new StringBuilder("[");
        for (Object[] fila : pedidosPorMes) {
            pedidosPorMesJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        pedidosPorMesJsonString.append("]");

        model.addAttribute("pedidosPorMes", pedidosPorMesJsonString);

        //porcentaje de pedidos por tipo de cliente (nuevos/recurrentes)
        List<Object[]> pedidosPorDiaDeSemana = pedidoService.countAllGroupByDiaDeSemana(fechaBusqueda);
        StringBuilder pedidosPorDiaDeSemanaJsonString = new StringBuilder("[");
        for (Object[] fila : pedidosPorDiaDeSemana) {
            pedidosPorDiaDeSemanaJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        pedidosPorDiaDeSemanaJsonString.append("]");

        model.addAttribute("pedidosPorDiaDeSemana", pedidosPorDiaDeSemanaJsonString);

        //tiempo por estado
        List<Object[]> tiempoPorEstado = pedidoService.avgTiempoByEstadoPedido();
        StringBuilder tiempoPorEstadoJsonString = new StringBuilder("[");
        for (Object[] fila : tiempoPorEstado) {
            tiempoPorEstadoJsonString.append("{").append("label:'").append(fila[0]).append("',value:").append(fila[1]).append("},");
        }
        tiempoPorEstadoJsonString.append("]");

        model.addAttribute("tiempoPorEstado", tiempoPorEstadoJsonString);

        //tiempo por estado
        List<Object[]> tiempoPorTipoPedido = pedidoService.avgTiempoByTipoPedido();
        StringBuilder tiempoPorTipoPedidoJsonString = new StringBuilder("[");
        for (Object[] fila : tiempoPorTipoPedido) {
            tiempoPorTipoPedidoJsonString.append("{").append("label:'").append(fila[0]).append("',value:").append(fila[1]).append("},");
        }
        tiempoPorTipoPedidoJsonString.append("]");

        model.addAttribute("tiempoPorTipoPedido", tiempoPorTipoPedidoJsonString);

        //Inventario
        //Cantidad reabastecimientos por mes
        List<Object[]> cantidadReabastecimientoPorMes = productoService.countAllReabastecimientosGroupByMes(fechaBusqueda);
        StringBuilder cantidadReabastecimientoPorMesJsonString = new StringBuilder("[");
        for (Object[] fila : cantidadReabastecimientoPorMes) {
            cantidadReabastecimientoPorMesJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        cantidadReabastecimientoPorMesJsonString.append("]");

        model.addAttribute("cantidadReabastecimientoPorMes", cantidadReabastecimientoPorMesJsonString);

        //Costo reabastecimiento por mes
        List<Object[]> costoReabastecimientoPorMes = productoService.sumAllReabastecimientosGroupByMes(fechaBusqueda);
        StringBuilder costoReabastecimientoPorMesJsonString = new StringBuilder("[");
        for (Object[] fila : costoReabastecimientoPorMes) {
            costoReabastecimientoPorMesJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        costoReabastecimientoPorMesJsonString.append("]");

        model.addAttribute("costoReabastecimientoPorMes", costoReabastecimientoPorMesJsonString);

        //Uso por tipo producto
        List<Object[]> usoPorTipoProducto = productoService.countAllUsoGroupByTipo();
        StringBuilder usoPorTipoProductoJsonString = new StringBuilder("[");
        for (Object[] fila : usoPorTipoProducto) {
            usoPorTipoProductoJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        usoPorTipoProductoJsonString.append("]");

        model.addAttribute("usoPorTipoProducto", usoPorTipoProductoJsonString);

        //Uso por tipo producto
        List<Object[]> costoPorTipoProducto = productoService.sumAllUsoGroupByTipo();
        StringBuilder costoPorTipoProductoJsonString = new StringBuilder("[");
        for (Object[] fila : costoPorTipoProducto) {
            costoPorTipoProductoJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        costoPorTipoProductoJsonString.append("]");

        model.addAttribute("costoPorTipoProducto", costoPorTipoProductoJsonString);

        //Proveedores por cantidad de reabastecimientos
        List<Object[]> proveedoresPorCantidadReabastecimientos = proveedorService.countAllReabastecimientosGroupByProveedor();
        StringBuilder proveedoresPorCantidadReabastecimientosJsonString = new StringBuilder("[");
        for (Object[] fila : proveedoresPorCantidadReabastecimientos) {
            proveedoresPorCantidadReabastecimientosJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        proveedoresPorCantidadReabastecimientosJsonString.append("]");

        model.addAttribute("proveedoresPorCantidadReabastecimientos", proveedoresPorCantidadReabastecimientosJsonString);

        //Proveedores por costos de reabastecimientos
        List<Object[]> proveedoresPorCostoReabastecimientos = proveedorService.sumAllReabastecimientosGroupByProveedor();
        StringBuilder proveedoresPorCostoReabastecimientosJsonString = new StringBuilder("[");
        for (Object[] fila : proveedoresPorCostoReabastecimientos) {
            proveedoresPorCostoReabastecimientosJsonString.append("{").append("x:'").append(fila[0]).append("',y:").append(fila[1]).append("},");
        }
        proveedoresPorCostoReabastecimientosJsonString.append("]");

        model.addAttribute("proveedoresPorCostoReabastecimientos", proveedoresPorCostoReabastecimientosJsonString);

        return "estadisticas";
    }

}
