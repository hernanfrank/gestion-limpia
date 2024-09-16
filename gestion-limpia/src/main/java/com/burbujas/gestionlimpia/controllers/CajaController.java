package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Gasto;
import com.burbujas.gestionlimpia.models.services.ICajaService;
import com.burbujas.gestionlimpia.models.services.IPedidoService;
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

    private final IPedidoService pedidoService;
    private final ICajaService cajaService;

    @Autowired
    public CajaController(IPedidoService pedidoService, ICajaService cajaService) {
        this.pedidoService = pedidoService;
        this.cajaService = cajaService;
    }

    @GetMapping(value = "/gasto/nuevo")
    public String crearGasto(Model model) {
        Gasto gasto = new Gasto();

        model.addAttribute("gasto", gasto);

        model.addAttribute("titulo", "Nuevo gasto");
        return "caja/gasto";
    }

    @PostMapping(value = "/gasto/guardar")
    public String guardarGasto(@Valid Gasto gasto, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "caja/gasto";
        }
        // recibimos el objeto gasto del formulario y lo persistimos
        cajaService.saveGasto(gasto);

        flashmsg.addFlashAttribute("messageType", "success");
        flashmsg.addFlashAttribute("message", "Listado de gastos actualizado");

        // redirigimos al listado
        return "redirect:/caja/gastos";
    }

    @GetMapping(value = {"/gastos"})
    public String listarGastos(Model model){
        model.addAttribute("titulo", "Listado de gastos");

        List<Gasto> gastos = cajaService.findAllGastosOrderByFechaDesc();
        model.addAttribute("gastos", gastos);

        return "caja/gastos";
    }

    @GetMapping(value = "/gasto/{id}")
    public String editarGasto(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Gasto gasto = cajaService.findGastoById(id);

        if (id > 0 && gasto != null) {
            model.addAttribute("titulo", "Editar gasto");
            model.addAttribute("gasto", gasto);

            return "caja/gasto";
        } else {
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "No se encontró el gasto.");
            return "redirect:/caja/gastos";
        }

    }

    @GetMapping("/gasto/eliminar/{id}")
    public ResponseEntity<Object> eliminarGasto(@PathVariable(name = "id") Long id){
        Gasto gasto = cajaService.findGastoById(id);
        if (gasto != null) {
            cajaService.deleteGasto(gasto);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El gasto ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el gasto.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
