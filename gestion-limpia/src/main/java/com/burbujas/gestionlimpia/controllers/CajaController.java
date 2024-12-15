package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.services.ICajaService;
import com.burbujas.gestionlimpia.models.services.IClienteService;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/caja")
public class CajaController {

    private final IProductoService productoService;
    private final IClienteService clienteService;
    private final ICajaService cajaService;

    @Autowired
    public CajaController(IProductoService productoService, IClienteService clienteService, ICajaService cajaService) {
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.cajaService = cajaService;
    }

    @GetMapping(value = "/movimientoCaja/nuevo")
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

    @PostMapping(value = "/movimientoCaja/guardar")
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

    @GetMapping(value = {"/movimientosCaja"})
    public String listarMovimientosCaja(Model model){
        model.addAttribute("titulo", "Listado de movimientos de caja");

        List<MovimientoCaja> movimientosCaja = cajaService.findAllMovimientosCajaOrderByFechaDesc();
        model.addAttribute("movimientosCaja", movimientosCaja);

        return "caja/movimientosCaja";
    }

    @GetMapping(value = "/movimientoCaja/{id}")
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

    @GetMapping("/movimientoCaja/eliminar/{id}")
    public ResponseEntity<Object> eliminarMovimientoCaja(@PathVariable(name = "id") Long id){
        MovimientoCaja movimientoCaja = cajaService.findMovimientoCajaById(id);
        if (movimientoCaja != null) {
            cajaService.deleteMovimientoCaja(movimientoCaja);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El movimiento de caja ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el movimiento de caja.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
