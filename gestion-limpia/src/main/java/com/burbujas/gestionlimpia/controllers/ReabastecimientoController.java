package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.MovimientoCaja;
import com.burbujas.gestionlimpia.models.entities.Producto;
import com.burbujas.gestionlimpia.models.entities.Proveedor;
import com.burbujas.gestionlimpia.models.entities.Reabastecimiento;
import com.burbujas.gestionlimpia.models.entities.enums.TipoMovimientoCaja;
import com.burbujas.gestionlimpia.models.services.ICajaService;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/productos/reabastecimientos")
public class ReabastecimientoController {

    private final IProductoService productoService;
    private final ICajaService cajaService;

    @Autowired
    public ReabastecimientoController(IProductoService productoService, ICajaService cajaService) {
        this.productoService = productoService;
        this.cajaService = cajaService;
    }

    @GetMapping("/listar/{idProducto}")
    public String listarReabastecimientosPorProducto(@PathVariable(value = "idProducto") Long idProducto, Model model){
        model.addAttribute("titulo", "Reabastecimientos");

        List<Reabastecimiento> reabastecimientos = this.productoService.findAllReabastecimientosByProductoId(idProducto);

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value= {"/listar", "/", ""})
    public String listarReabastecimientos(Model model){
        model.addAttribute("titulo", "Reabastecimientos");
        model.addAttribute("mostrarEliminados", false);

        List<Producto> productos = this.productoService.findAll();

        List<Reabastecimiento> reabastecimientos = new ArrayList<>();
        productos.forEach(producto -> reabastecimientos.addAll(producto.getReabastecimientos()));

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value= {"/eliminados", })
    public String listarReabastecimientosEliminados(Model model){
        model.addAttribute("titulo", "Reabastecimientos eliminados");
        model.addAttribute("mostrarEliminados", true);

        List<Object[]> result = this.productoService.findAllReabastecimientosEliminados();
        List<Reabastecimiento> reabastecimientos = new ArrayList<>();
        for(Object[] fila : result){
            Proveedor proveedor = new Proveedor();
            Producto producto = new Producto();
            Reabastecimiento reabastecimiento = new Reabastecimiento();

            reabastecimiento.setId((Long) fila[0]);
            reabastecimiento.setFecha((Date) fila[1]);
            reabastecimiento.setCantidadProducto((Double) fila[3]);
            reabastecimiento.setPrecio((Double) fila[4]);

            proveedor.setNombre((String) fila[5]);
            producto.setTipo((String) fila[2]);

            reabastecimiento.setProveedor(proveedor);
            reabastecimiento.setProducto(producto);

            reabastecimientos.add(reabastecimiento);
        }

        model.addAttribute("reabastecimientos", reabastecimientos);

        return "inventario/reabastecimientos/reabastecimientos";
    }

    @GetMapping(value = "/nuevo")
    public String crearReabastecimiento(Model model) {
        model.addAttribute("titulo", "Nuevo producto");

        Reabastecimiento reabastecimiento = new Reabastecimiento();
        List<Producto> productos = this.productoService.findAll();
        List<Proveedor> proveedores = this.productoService.findAllProveedores();

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
        Producto producto = this.productoService.findById(idProducto);
        if(producto != null) {
            reabastecimiento.setProducto(producto);

            List<Producto> productos = this.productoService.findAll();
            List<Proveedor> proveedores = this.productoService.findAllProveedores();
            reabastecimiento.setCantidadProducto(producto.getCantidadActual());

            model.addAttribute("reabastecimiento", reabastecimiento);
            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select

            return "inventario/reabastecimientos/reabastecimiento";
        }else{
            flashmsg.addFlashAttribute("messageType","error");
            flashmsg.addFlashAttribute("message","Error al crear Reabastecimiento. No se encontró el producto.");
            return "redirect:/productos/listar";
        }
    }

    @GetMapping(value = "/editar/{id}")
    public String editarReabastecimiento(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {
        Reabastecimiento reabastecimiento = this.productoService.findReabastecimientoById(id);
        if (id > 0 && reabastecimiento != null) {
            List<Producto> productos = this.productoService.findAll();
            List<Proveedor> proveedores = this.productoService.findAllProveedores();

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
            List<Producto> productos = this.productoService.findAll();
            List<Proveedor> proveedores = this.productoService.findAllProveedores();

            model.addAttribute("productos", productos); // para el select
            model.addAttribute("proveedores", proveedores); // para el select
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "inventario/reabastecimientos/reabastecimiento";
        }
        Producto producto = reabastecimiento.getProducto();
        if(producto != null) {
            this.productoService.saveReabastecimiento(reabastecimiento);
            this.productoService.updateCantidadActual(producto);

            MovimientoCaja movimientoGenerado = new MovimientoCaja();
            movimientoGenerado.setReabastecimiento(reabastecimiento);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            movimientoGenerado.setDescripcion("Generado por reabastecimiento: "+reabastecimiento.getCantidadProducto()+" unidades de "+reabastecimiento.getProducto().getTipo()+" del proveedor "+reabastecimiento.getProveedor().getNombre()+" en la fecha "+formatter.format(reabastecimiento.getFecha())+".");
            movimientoGenerado.setFecha(reabastecimiento.getFecha());
            movimientoGenerado.setTipoMovimientoCaja(TipoMovimientoCaja.EGRESO);
            movimientoGenerado.setTipoCaja(reabastecimiento.getTipoCaja());
            movimientoGenerado.setProveedor(reabastecimiento.getProveedor());
            movimientoGenerado.setMonto(reabastecimiento.getPrecio());
            cajaService.saveMovimientoCaja(movimientoGenerado);

            flashmsg.addFlashAttribute("messageType","success");
            flashmsg.addFlashAttribute("message","Listado de reabastecimientos actualizado.");
        }else{
            flashmsg.addFlashAttribute("messageType","success");
            flashmsg.addFlashAttribute("message","Error al guardar el reabastecimiento. Producto no encontrado.");
        }
        return "redirect:/productos/reabastecimientos/listar";
    }

    @PostMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminarReabastecimiento(@PathVariable(name = "id") Long id){
        Reabastecimiento reabastecimiento = this.productoService.findReabastecimientoById(id);
        if (reabastecimiento != null) {
            Producto producto = reabastecimiento.getProducto();
            if(producto != null) {
                this.productoService.deleteReabastecimiento(producto, reabastecimiento);
                this.productoService.updateCantidadActual(producto);
                return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El reabastecimiento ha sido eliminado correctamente.\"}", HttpStatus.OK);
            }else{
                return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el producto.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el reabastecimiento.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/restaurar/{id}")
    public ResponseEntity<Object> restaurarReabastecimiento(@PathVariable(name = "id") Long id){
        try{
            this.productoService.restaurarReabastecimiento(id);
            Producto producto = this.productoService.findReabastecimientoById(id).getProducto();
            this.productoService.updateCantidadActual(producto);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El reabastecimiento ha sido restaurado correctamente.\"}", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al restaurar."+e+"\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
