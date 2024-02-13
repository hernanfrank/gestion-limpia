package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;

    @Autowired
    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    //PRODUCTOS
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

    //REABASTECIMIENTOS
    @GetMapping("/reabastecimientos/listar/{idProducto}")
    public String listarReabastecimientos(@PathVariable(value = "idProducto") Long idProducto, Model model){
        model.addAttribute("titulo", "Reabastecimientos");

        List<Reabastecimiento> reabastecimientos = productoService.findAllReabastecimientosByProductoId(idProducto);

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping("/reabastecimientos/listar")
    public String listarReabastecimientos(Model model){
        model.addAttribute("titulo", "Reabastecimientos");

        List<Producto> productos = productoService.findAll();

        List<Reabastecimiento> reabastecimientos = new ArrayList<>();
        productos.forEach(producto -> reabastecimientos.addAll(producto.getReabastecimientos()));

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value = "/reabastecimientos/nuevo")
    public String crearReabastecimiento(Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Reabastecimiento reabastecimiento = new Reabastecimiento();
        List<Producto> productos = productoService.findAll();

        model.addAttribute("reabastecimiento", reabastecimiento);
        model.addAttribute("productos", productos); // para el select
        model.addAttribute("fechaActual", new Date());

        return "inventario/reabastecimientos/reabastecimiento";
    }

    @GetMapping(value = "/reabastecimientos/nuevo/{idProducto}")
    public String crearReabastecimiento(@PathVariable(value = "idProducto") Long idProducto, RedirectAttributes flashmsg, Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Reabastecimiento reabastecimiento = new Reabastecimiento();

        // buscamos y asignamos el producto obtenido por url
        Producto producto = productoService.findById(idProducto);
        if(producto != null) {
            reabastecimiento.setProducto(producto);

            List<Producto> productos = productoService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();

            model.addAttribute("reabastecimiento", reabastecimiento);
            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select
            model.addAttribute("fechaActual", new Date());

            return "inventario/reabastecimientos/reabastecimiento";
        }else{
            flashmsg.addFlashAttribute("danger", "Error al crear Reabastecimiento. No se encontró el producto.");
            return "redirect:/productos/inventario";
        }
    }

    @GetMapping(value = "/reabastecimientos/editar/{id}")
    public String editarReabastecimiento(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Reabastecimiento reabastecimiento = productoService.findReabastecimientoById(id);
        if (id > 0 && reabastecimiento != null) {
            List<Producto> productos = productoService.findAll();
            List<Proveedor> proveedores = productoService.findAllProveedores();

            model.addAttribute("reabastecimiento", reabastecimiento);
            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select
            model.addAttribute("fechaActual", new Date());

            model.addAttribute("titulo", "Editar reabastecimiento");
            return "inventario/reabastecimientos/reabastecimiento";
        } else {
            flashmsg.addFlashAttribute("danger", "No se encontró el reabastecimiento.");
            return "redirect:/productos/reabastecimientos/listar";
        }

    }

    @PostMapping(value = "/reabastecimientos/guardar")
    public String guardarReabastecimiento(@Valid Reabastecimiento reabastecimiento, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/reabastecimientos/reabastecimiento";
        }
        Producto producto = reabastecimiento.getProducto();
        if(producto != null) {
            productoService.saveReabastecimiento(reabastecimiento);
            productoService.addReabastecimiento(producto, reabastecimiento);

            flashmsg.addFlashAttribute("success", "Listado de reabastecimientos actualizado");
        }else{
            flashmsg.addFlashAttribute("danger", "Error al guardar el reabastecimiento. Producto no encontrado.");
        }
        return "redirect:/productos/reabastecimientos/listar";
    }

    @GetMapping("/reabastecimientos/eliminar/{id}")
    public String eliminarReabastecimiento(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Reabastecimiento reabastecimiento = productoService.findReabastecimientoById(id);
        if (reabastecimiento != null) {
            Producto producto = reabastecimiento.getProducto();
            if(producto != null) {
                productoService.deleteReabastecimiento(producto, reabastecimiento);
                flashmsg.addFlashAttribute("success", "Reabastecimiento eliminado correctamente.");
            }else{
                flashmsg.addFlashAttribute("danger", "Error al eliminar el reabastecimiento. Producto no encontrado.");
            }
        }
        flashmsg.addFlashAttribute("danger", "Error al eliminar. No se encontró el reabastecimiento.");

        return "redirect:/productos/reabastecimientos/listar";
    }

    //PROVEEDORES

    @GetMapping("/proveedores/listar")
    public String listarProveedores(Model model){
        model.addAttribute("titulo", "Proveedores");

        List<Proveedor> proveedores = productoService.findAllProveedores();

        model.addAttribute("proveedores", proveedores);

        return "inventario/proveedores/proveedores";
    }

    @GetMapping(value = "/proveedores/nuevo")
    public String crearProveedor(Model model) {
        model.addAttribute("titulo", "Nuevo proveedor");

        Proveedor proveedor = new Proveedor();

        model.addAttribute("proveedor", proveedor);

        return "inventario/proveedores/proveedor";
    }

    @GetMapping(value = "/proveedores/editar/{id}")
    public String editarProveedores(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Proveedor proveedor = productoService.findProveedorById(id);
        if (id > 0 && proveedor != null) {

            model.addAttribute("proveedor", proveedor);

            model.addAttribute("titulo", "Editar proveedor");
            return "inventario/proveedores/proveedor";
        } else {
            flashmsg.addFlashAttribute("danger", "No se encontró el proveedor.");
            return "redirect:/productos/proveedores/listar";
        }

    }

    @PostMapping(value = "/proveedores/guardar")
    public String guardarProveedor(@Valid Proveedor proveedor, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/proveedores/proveedor";
        }

        productoService.saveProveedor(proveedor);

        flashmsg.addFlashAttribute("success", "Listado de reabastecimientos actualizado");

        return "redirect:/productos/proveedores/listar";
    }

    @GetMapping("/proveedores/eliminar/{id}")
    public String eliminarProveedor(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Proveedor proveedor = productoService.findProveedorById(id);
        if (proveedor != null) {
            if(proveedor.getReabastecimientos().size() > 0){
                // antes de borrar el proveedor, borro todos sus reabastecimientos si tiene
                proveedor.getReabastecimientos().forEach(reabastecimiento -> productoService.deleteReabastecimiento(reabastecimiento.getProducto(),reabastecimiento));
            }
            productoService.deleteProveedor(id);
            flashmsg.addFlashAttribute("success", "Proveedor eliminado correctamente.");
        }
        flashmsg.addFlashAttribute("danger", "Error al eliminar. No se encontró el proveedor.");

        return "redirect:/productos/proveedores/listar";
    }
}
