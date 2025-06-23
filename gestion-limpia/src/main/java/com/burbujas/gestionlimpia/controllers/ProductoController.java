package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.*;
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

import java.sql.Timestamp;
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
            // sólo si es un producto nuevo
            tiposPedido.forEach(
                    tipoPedido -> {
                        TipoPedidoProductoMapping tipoPedidoProductoMappingAux = new TipoPedidoProductoMapping();
                        tipoPedidoProductoMappingAux.setProducto(producto);
                        tipoPedidoProductoMappingAux.setTipoPedido(tipoPedido);
                        tipoPedidoProductoMappingAux.setCantidadUsada(0.d);
                        tiposPedidoProducto.add(tipoPedidoProductoMappingAux);
                    }
            );
            producto.setTipoPedidoProductoMapping(tiposPedidoProducto);
        }

        model.addAttribute("producto", producto);
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Producto producto, BindingResult result, Model model, RedirectAttributes flashmsg) {
        fillTipoPedidoProductoMappingArray(model, producto);
        if (result.hasErrors()) {
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/producto";
        }
        this.productoService.save(producto);

        flashmsg.addFlashAttribute("messageType", "success");
        flashmsg.addFlashAttribute("message", "Listado de productos actualizado.");

        return "redirect:/productos/inventario";
    }

    @GetMapping(value = {"/listar", "/", "" })
    public String listar(Model model){
        model.addAttribute("titulo", "Listado de productos");
        model.addAttribute("mostrarEliminados", false);

        List<Producto> productos = this.productoService.findAll();

        model.addAttribute("productos", productos);

        return "inventario/inventario";
    }

    @GetMapping(value = {"/eliminados"})
    public String listarEliminados(Model model){
        model.addAttribute("titulo", "Productos eliminados");
        model.addAttribute("mostrarEliminados", true);

        List<Object[]> result = this.productoService.findAllProductosEliminados();
        List<Producto> productos = new ArrayList<>();
        for(Object[] fila : result){
            Producto producto = new Producto();
            Reabastecimiento reabastecimiento = new Reabastecimiento();

            producto.setId((Long) fila[0]);
            producto.setTipo((String) fila[1]);
            producto.setCantidadActual((Double) fila[2]);
            producto.setUmbralAvisoReabastecimiento((Double) fila[3]);
            producto.setEliminado(true);

            reabastecimiento.setProducto(producto);
            reabastecimiento.setFecha((Timestamp) fila[4]);

            producto.setReabastecimientos(List.of(reabastecimiento));

            productos.add(producto);
        }

        model.addAttribute("productos", productos);

        return "inventario/inventario";
    }

    @GetMapping(value = "/editar/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Producto producto = this.productoService.findById(id);

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

    @PostMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable(name = "id") Long id){
        Producto producto = this.productoService.findById(id);
        if (producto != null) {
            this.productoService.delete(id);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El producto ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el producto.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/restaurar/{id}")
    public ResponseEntity<Object> restaurar(@PathVariable(name = "id") Long id){
        try{
            this.productoService.restaurar(id);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El producto ha sido restaurado correctamente.\"}", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al restaurar. "+e+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
