package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Proveedor;
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

import java.util.List;

@Controller
@RequestMapping("/productos/proveedores")
public class ProveedorController {

    private final IProductoService productoService;

    @Autowired
    public ProveedorController(IProductoService productoService) {
        this.productoService = productoService;
    }


    @GetMapping(value = {"/listar", "/", ""})
    public String listarProveedores(Model model){
        model.addAttribute("titulo", "Proveedores");

        List<Proveedor> proveedores = productoService.findAllProveedores();

        model.addAttribute("proveedores", proveedores);

        return "inventario/proveedores/proveedores";
    }

    @GetMapping(value = "/nuevo")
    public String crearProveedor(Model model) {
        model.addAttribute("titulo", "Nuevo proveedor");

        Proveedor proveedor = new Proveedor();

        model.addAttribute("proveedor", proveedor);

        return "inventario/proveedores/proveedor";
    }

    @GetMapping(value = "/editar/{id}")
    public String editarProveedores(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Proveedor proveedor = productoService.findProveedorById(id);
        if (id > 0 && proveedor != null) {

            model.addAttribute("proveedor", proveedor);

            model.addAttribute("titulo", "Editar proveedor");
            return "inventario/proveedores/proveedor";
        } else {
            flashmsg.addFlashAttribute("messageType","error");
            flashmsg.addFlashAttribute("message","No se encontró el proveedor.");
            return "redirect:/productos/proveedores/listar";
        }

    }

    @PostMapping(value = "/guardar")
    public String guardarProveedor(@Valid Proveedor proveedor, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) {
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/proveedores/proveedor";
        }

        productoService.saveProveedor(proveedor);

        flashmsg.addFlashAttribute("messageType","success");
        flashmsg.addFlashAttribute("message","Listado de proveedores actualizado.");

        return "redirect:/productos/proveedores/listar";
    }

    @GetMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminarProveedor(@PathVariable(name = "id") Long id){
        Proveedor proveedor = productoService.findProveedorById(id);
        if (proveedor != null) {
            if(proveedor.getReabastecimientos().size() > 0){
                // antes de borrar el proveedor, borro todos sus reabastecimientos si tiene
                proveedor.getReabastecimientos().forEach(reabastecimiento -> productoService.deleteReabastecimiento(reabastecimiento.getProducto(),reabastecimiento));
            }
            productoService.deleteProveedor(id);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El proveedor ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el proveedor.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
