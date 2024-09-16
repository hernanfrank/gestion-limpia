package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.entities.TipoPedidoProductoMapping;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import com.burbujas.gestionlimpia.models.services.ITipoPedidoService;
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
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final IProductoService productoService;
    private final ITipoPedidoService tipoPedidoService;


    @Autowired
    public ProductoController(IProductoService productoService, ITipoPedidoService tipoPedidoService) {
        this.productoService = productoService;
        this.tipoPedidoService = tipoPedidoService;
    }

    @GetMapping(value = "/nuevo")
    public String crear(Model model) {
        Producto producto = new Producto();

        fillTipoPedidoProductoMappingArray(model, producto);
        model.addAttribute("titulo", "Nuevo producto");
        return "inventario/producto";
    }

    private void fillTipoPedidoProductoMappingArray(Model model, Producto producto) {
        // si no tiene mapeos de cantidad de producto usado por pedido, creo unos ficticios para poder linkearlos a la clase

        List<TipoPedido> tiposPedido = tipoPedidoService.findAll();
        List<TipoPedidoProductoMapping> tiposPedidoProducto  = producto.getTipoPedidoProductoMapping();

        if(tiposPedidoProducto.size() == 0){
            tiposPedido.forEach(
                    tipoPedido -> {
                        TipoPedidoProductoMapping tipoPedidoProductoMappingAux = new TipoPedidoProductoMapping();
                        tipoPedidoProductoMappingAux.setProducto(producto);
                        tipoPedidoProductoMappingAux.setTipoPedido(tipoPedido);
                        tipoPedidoProductoMappingAux.setCantidadUsada(0.d);
                        tiposPedidoProducto.add(tipoPedidoProductoMappingAux);
                    }
            );
        }

        producto.setTipoPedidoProductoMapping(tiposPedidoProducto);
        model.addAttribute("producto", producto);
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model, RedirectAttributes flashmsg) {
        if (result.hasErrors()) {
            // por alguna razón se pierden los reabastecimientos e historial de usos, por lo que los traemos nuevamente
            producto.setReabastecimientos(productoService.findById(producto.getId()).getReabastecimientos());
            producto.setHistorialProductoPedidos(productoService.findById(producto.getId()).getHistorialProductoPedidos());
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/producto";
        }
        productoService.save(producto);

        flashmsg.addFlashAttribute("success", "Listado de productos actualizado");

        return "redirect:/productos/inventario";
    }

    @GetMapping(value = {"/inventario", "/", "" })
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
            fillTipoPedidoProductoMappingArray(model, producto);
            model.addAttribute("titulo", "Editar producto");
            return "inventario/producto";
        } else {
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "No se encontró el producto.");
            return "redirect:/productos/inventario";
        }

    }

    @GetMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.delete(id);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El producto ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el producto.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
