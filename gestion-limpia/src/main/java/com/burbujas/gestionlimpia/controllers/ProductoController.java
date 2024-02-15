package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;

    @Autowired
    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping(value = "/nuevo")
    public String crear(Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Producto producto = new Producto();

        model.addAttribute("producto", producto);
        return "inventario/producto";
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/producto";
        }

        productoService.save(producto);

        flashmsg.addFlashAttribute("success", "Listado de productos actualizado");

        return "redirect:/productos/inventario";
    }

    @GetMapping(value = {"/inventario", "" })
    public String listar(Model model){
        model.addAttribute("titulo", "Listado de productos");

        List<Producto> productos = productoService.findAll();

        model.addAttribute("productos", productos);

        return "inventario/inventario";
    }

    @GetMapping(value = "/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Producto producto = productoService.findById(id);

        if (id > 0 && producto != null) {
            model.addAttribute("producto", producto);
            model.addAttribute("titulo", "Editar producto");
            return "inventario/producto";
        } else {
            flashmsg.addFlashAttribute("danger", "No se encontró el producto.");
            return "redirect:/productos/inventario";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Producto producto = productoService.findById(id);
        if (producto != null) {
            // antes de eliminar el producto elimino sus reabastecimientos
            List<Reabastecimiento> reabastecimientos = producto.getReabastecimientos();
            reabastecimientos.forEach(reabastecimiento -> productoService.deleteReabastecimiento(producto, reabastecimiento));
            productoService.delete(id);
            flashmsg.addFlashAttribute("success", "Producto eliminado correctamente.");
        }
        flashmsg.addFlashAttribute("danger", "Error al eliminar.<br>No se encontró el producto.");

        return "redirect:/productos/inventario";
    }

}
