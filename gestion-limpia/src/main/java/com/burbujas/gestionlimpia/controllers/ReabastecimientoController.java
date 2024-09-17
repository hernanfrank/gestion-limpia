package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/productos/reabastecimientos")
public class ReabastecimientoController {

    private final IProductoService productoService;

    @Autowired
    public ReabastecimientoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/listar/{idProducto}")
    public String listarReabastecimientos(@PathVariable(value = "idProducto") Long idProducto, Model model){
        model.addAttribute("titulo", "Reabastecimientos");

        List<Reabastecimiento> reabastecimientos = productoService.findAllReabastecimientosByProductoId(idProducto);

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value= {"/listar", "/", ""})
    public String listarReabastecimientos(Model model){
        model.addAttribute("titulo", "Reabastecimientos");

        List<Producto> productos = productoService.findAll();

        List<Reabastecimiento> reabastecimientos = new ArrayList<>();
        productos.forEach(producto -> reabastecimientos.addAll(producto.getReabastecimientos()));

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value = "/nuevo")
    public String crearReabastecimiento(Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Reabastecimiento reabastecimiento = new Reabastecimiento();
        List<Producto> productos = productoService.findAll();
        List<Proveedor> proveedores = productoService.findAllProveedores();

        model.addAttribute("reabastecimiento", reabastecimiento);
        model.addAttribute("productos", productos); // para el select
        model.addAttribute("proveedores", proveedores); // para el select

        return "inventario/reabastecimientos/reabastecimiento";
    }

    @GetMapping(value = "/nuevo/{idProducto}")
    public String crearReabastecimiento(@PathVariable(value = "idProducto") Long idProducto, RedirectAttributes flashmsg, Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Reabastecimiento reabastecimiento = new Reabastecimiento();

        // buscamos y asignamos el producto obtenido por url
        Producto producto = productoService.findById(idProducto);
        if(producto != null) {
            reabastecimiento.setProducto(producto);

            List<Producto> productos = productoService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();
            reabastecimiento.setCantidadProducto(producto.getCantidadActual());

            model.addAttribute("reabastecimiento", reabastecimiento);
            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select

            return "inventario/reabastecimientos/reabastecimiento";
        }else{
            flashmsg.addFlashAttribute("messageType","error");
            flashmsg.addFlashAttribute("message","Error al crear Reabastecimiento. No se encontró el producto.");
            return "redirect:/productos/inventario";
        }
    }

    @GetMapping(value = "/editar/{id}")
    public String editarReabastecimiento(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Reabastecimiento reabastecimiento = productoService.findReabastecimientoById(id);
        if (id > 0 && reabastecimiento != null) {
            List<Producto> productos = productoService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();

            model.addAttribute("reabastecimiento", reabastecimiento);
            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select

            model.addAttribute("titulo", "Editar reabastecimiento");
            return "inventario/reabastecimientos/reabastecimiento";
        } else {
            flashmsg.addFlashAttribute("messageType","error");
            flashmsg.addFlashAttribute("message","No se encontró el reabastecimiento.");
            return "redirect:/productos/reabastecimientos/listar";
        }

    }

    @PostMapping(value = "/guardar")
    public String guardarReabastecimiento(@Valid Reabastecimiento reabastecimiento, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) {
            List<Producto> productos = productoService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();

            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/reabastecimientos/reabastecimiento";
        }
        Producto producto = reabastecimiento.getProducto();
        if(producto != null) {
            productoService.saveReabastecimiento(reabastecimiento);
            productoService.updateCantidadActual(producto);

            flashmsg.addFlashAttribute("messageType","success");
            flashmsg.addFlashAttribute("message","Listado de reabastecimientos actualizado.");
        }else{
            flashmsg.addFlashAttribute("messageType","success");
            flashmsg.addFlashAttribute("message","Error al guardar el reabastecimiento. Producto no encontrado.");
        }
        return "redirect:/productos/reabastecimientos/listar";
    }

    @GetMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminarReabastecimiento(@PathVariable(name = "id") Long id){
        Reabastecimiento reabastecimiento = productoService.findReabastecimientoById(id);
        if (reabastecimiento != null) {
            Producto producto = reabastecimiento.getProducto();
            if(producto != null) {
                producto.setCantidadActual(producto.getCantidadActual() - reabastecimiento.getCantidadProducto());
                productoService.save(producto);
                productoService.deleteReabastecimiento(producto, reabastecimiento);
                return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El reabastecimiento ha sido eliminado correctamente.\"}", HttpStatus.OK);
            }else{
                return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el producto.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el reabastecimiento.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
