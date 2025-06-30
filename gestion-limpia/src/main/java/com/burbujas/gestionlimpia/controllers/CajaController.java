package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.entities.enums.TipoCaja;
import com.burbujas.gestionlimpia.models.services.ICajaService;
import com.burbujas.gestionlimpia.models.services.IClienteService;
import com.burbujas.gestionlimpia.models.services.IPedidoService;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final ICajaService cajaService;
    private final IPedidoService pedidoService;

    @Autowired
    public CajaController(IProductoService productoService, IClienteService clienteService, ICajaService cajaService, IPedidoService pedidoService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.cajaService = cajaService;
        this.pedidoService = pedidoService;
    }

    @GetMapping(value = "/movimientosCaja/nuevo")
    public String crearMovimientoCaja(Model model) {
        MovimientoCaja movimientoCaja = new MovimientoCaja();

        model.addAttribute("movimientoCaja", movimientoCaja);

        model.addAttribute("titulo", "Nuevo movimiento de caja");

        List<Cliente> clientes = clienteService.findAll();
        List<Proveedor> proveedores = productoService.findAllProveedores();

        model.addAttribute("clientes", clientes);
        model.addAttribute("proveedores", proveedores);

        return "caja/movimientoCaja";
    }

    @PostMapping(value = "/movimientosCaja/guardar")
    public String guardarMovimientoCaja(@Valid MovimientoCaja movimientoCaja, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "movimientoCaja";
        }
        // recibimos el objeto movimientoCaja del formulario y lo persistimos
        cajaService.saveMovimientoCaja(movimientoCaja);

        flashmsg.addFlashAttribute("messageType", "success");
        flashmsg.addFlashAttribute("message", "Listado de movimientos de caja actualizado");

        // redirigimos al listado
        return "redirect:/caja/movimientosCaja";
    }

    private static void setModelAttributesListadoMovimientosCaja(Model model, List<MovimientoCaja> movimientosCaja, Date fechaDesde, Date fechaHasta) {
        model.addAttribute("titulo", "Listado de movimientos de caja");
        model.addAttribute("movimientosCaja", movimientosCaja);

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        model.addAttribute("fechaDesde", formatter.format(fechaDesde));
        model.addAttribute("fechaHasta", formatter.format(fechaHasta));

        model.addAttribute("totalMovimientosCajaEfectivo",
                //calculamos el total de caja efectivo, ND y Egreso son negativos
                movimientosCaja.stream()
                        .filter(movimientoCajaEfectivo -> movimientoCajaEfectivo.getTipoCaja() == TipoCaja.EFECTIVO)
                        .mapToDouble(movimientoCaja -> {
                            return switch (movimientoCaja.getTipoMovimientoCaja()) {
                                case NOTACREDITO, EGRESO -> -movimientoCaja.getMonto();
                                default -> movimientoCaja.getMonto();
                            };
                        }).sum()
        );
        model.addAttribute("totalMovimientosCajaBanco",
                //calculamos el total de caja banco, ND y Egreso son negativos
                movimientosCaja.stream()
                        .filter(movimientoCajaBanco -> movimientoCajaBanco.getTipoCaja() == TipoCaja.BANCO)
                        .mapToDouble(movimientoCaja -> {
                            return switch (movimientoCaja.getTipoMovimientoCaja()) {
                                case NOTACREDITO, EGRESO -> -movimientoCaja.getMonto();
                                default -> movimientoCaja.getMonto();
                            };
                        }).sum()
        );
    }

    @GetMapping(value = {"/movimientosCaja/{fechaDesde}/{fechaHasta}"})
    public String listarMovimientosCaja(@PathVariable(value = "fechaDesde") String fechaDesde, @PathVariable(value = "fechaHasta") String fechaHasta, RedirectAttributes flashmsg, Model model) {
        model.addAttribute("titulo", "Movimientos de caja");
        model.addAttribute("mostrarEliminados", false);
        try{
            Date fechaDesdeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fechaDesde+" 00:00:00");
            Date fechaHastaDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fechaHasta+" 23:59:59");

            List<MovimientoCaja> movimientosCaja = cajaService.findAllByFechaAfterAndFechaBeforeOrderByFechaDesc(fechaDesdeDate, fechaHastaDate);

            setModelAttributesListadoMovimientosCaja(model, movimientosCaja, fechaDesdeDate, fechaHastaDate);

            return "caja/movimientosCaja";
        }catch (Exception e){
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "Ha ocurrido un error. Revise las fechas del período que se quiere consultar.");
            return "redirect:/caja/movimientosCaja";
        }

    }

    @GetMapping(value = {"/movimientosCaja/listar", "/movimientosCaja"})
    public String listarMovimientosCaja(Model model){
        model.addAttribute("titulo", "Movimientos de caja");
        model.addAttribute("mostrarEliminados", false);

        List<MovimientoCaja> movimientosCaja = cajaService.findAllMovimientosCajaOrderByFechaDesc();
        Date fechaDesdeDate = new Date();
        Date fechaHastaDate = new Date();
        if(!movimientosCaja.isEmpty()) {
            fechaDesdeDate = movimientosCaja.get(movimientosCaja.size() - 1).getFecha();// fecha del movimiento más antigüo
            fechaHastaDate = movimientosCaja.get(0).getFecha(); // fecha del movimiento más nuevo
        }
        setModelAttributesListadoMovimientosCaja(model, movimientosCaja, fechaDesdeDate, fechaHastaDate);

        return "caja/movimientosCaja";
    }

    @GetMapping(value = {"/movimientosCaja/eliminados"})
    public String listarMovimientosCajaEliminados(Model model){
        model.addAttribute("titulo", "Movimientos eliminados");
        model.addAttribute("mostrarEliminados", true);

        List<MovimientoCaja> movimientosCaja = cajaService.findAllMovimientosCajaEliminados();

        model.addAttribute("movimientosCaja", movimientosCaja);

        return "caja/movimientosCaja";
    }

    @GetMapping(value = "/movimientosCaja/editar/{id}")
    public String editarMovimientoCaja(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        MovimientoCaja movimientoCaja = cajaService.findMovimientoCajaById(id);

        if (id > 0 && movimientoCaja != null) {
            model.addAttribute("titulo", "Editar movimiento de caja");
            model.addAttribute("movimientoCaja", movimientoCaja);

            List<Cliente> clientes = clienteService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();

            model.addAttribute("clientes", clientes);
            model.addAttribute("proveedores", proveedores);

            return "caja/movimientoCaja";
        } else {
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "No se encontró el movimiento de caja.");
            return "redirect:/caja/movimientosCaja";
        }

    }

    @PostMapping("/movimientosCaja/eliminar/{id}")
    public ResponseEntity<Object> eliminarMovimientoCaja(@PathVariable(name = "id") Long id){
        try{
            MovimientoCaja movimientoCaja = cajaService.findMovimientoCajaById(id);
            if (movimientoCaja != null) {
                if(movimientoCaja.getPedido() != null){
                    // si es generado por un pedido cobrado, eliminamos también ese pedido
                    try{
                        pedidoService.deleteById(movimientoCaja.getPedido().getId());
                    }catch (Exception e){
                        return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar el movimiento. No se pudo eliminar el pedido asociado.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                cajaService.deleteMovimientoCaja(movimientoCaja);
                return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El movimiento de caja ha sido eliminado correctamente.\"}", HttpStatus.OK);
            }else{
                return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el movimiento de caja.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar el movimiento. "+e+" \"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/movimientosCaja/restaurar/{id}")
    public ResponseEntity<Object> restaurarMovimientoCaja(@PathVariable(name = "id") Long id) {
        try {
            this.cajaService.restaurarMovimientoCaja(id);
            return new ResponseEntity<>(
                    "{\"status\":\"OK\",\"msg\": \"El movimiento de caja ha sido restaurado correctamente.\"}",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "{\"status\":\"ERROR\",\"msg\": \"Error al restaurar el movimiento de caja. " + e.getMessage() + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/descargarReporte/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<?> descargarReporteCaja(@PathVariable(name = "fechaDesde") String fechaDesde, @PathVariable(name = "fechaHasta") String fechaHasta) {
        try {
            String nombreArchivo = "movimientos_caja_" + fechaDesde + "_a_" + fechaHasta + ".xlsx";
            Date fechaDesdeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fechaDesde+" 00:00:00");
            Date fechaHastaDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(fechaHasta+" 23:59:59");
            File archivo = cajaService.exportarMovimientosCaja(fechaDesdeDate, fechaHastaDate, nombreArchivo);

            if (!archivo.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo generar el reporte de caja.");
            }

            InputStreamResource recurso = new InputStreamResource(new FileInputStream(archivo));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + archivo.getName())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(archivo.length())
                    .body(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el reporte de caja: " + e.getMessage());
        }
    }

}
