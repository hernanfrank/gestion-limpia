package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.*;
import com.burbujas.gestionlimpia.models.entities.enums.EstadoPedido;
import com.burbujas.gestionlimpia.models.services.IClienteService;
import com.burbujas.gestionlimpia.models.services.IPedidoService;
import com.burbujas.gestionlimpia.models.services.IProductoService;
import com.burbujas.gestionlimpia.models.services.ITipoPedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class PedidoController {

    private final IPedidoService pedidoService;
    private final ITipoPedidoService tipoPedidoService;
    private final IClienteService clienteService;
    private final IProductoService productoService;

    @Autowired
    public PedidoController(IPedidoService pedidoService, ITipoPedidoService tipoPedidoService, IClienteService clienteService, IProductoService productoService) {
        this.pedidoService = pedidoService;
        this.tipoPedidoService = tipoPedidoService;
        this.clienteService = clienteService;
        this.productoService = productoService;
    }

    @GetMapping(value = {"/", ""})
    public String listarIndex(Model model){
        List<Pedido> pedidos = pedidoService.findAllByOrderByPrioridadDesc();
        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("pedidos", pedidos);
        return "index";
    }

    @GetMapping(value = {"/pedidos/nuevo/{last}"})
    public String crear(@PathVariable(value = "last") String last, Model model) {
        model.addAttribute("titulo", "Nuevo pedido");
        if(Objects.equals(last, "index")){
            model.addAttribute("last", "");
        }else{
            model.addAttribute("last", "pedidos/listar");
        }

        // instanciamos un pedido
        Pedido pedido = new Pedido();
        List<TipoPedido> tiposPedido = tipoPedidoService.findAll();
        List<Cliente> clientes = clienteService.findAll();

        // mapeamos el pedido y listado de clientes al formulario
        model.addAttribute("pedido", pedido);
        model.addAttribute("clientes", clientes);
        model.addAttribute("tiposPedido", tiposPedido);
        return "pedidos/pedido";
    }

    @PostMapping(value = "/pedidos/guardar")
    public String guardar(@Valid Pedido pedido, BindingResult result, Model model, RedirectAttributes flashmsg) {
        if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "pedidos/pedido";
        }
        if(pedido.getId() != null) {// sólo si el pedido no es nuevo
            // recién acá busco el historial de estados porque sino debería buscarlo cada ves que entra a la edición el pedido, de esta forma solo lo busca cuando se guarda...
            List<HistorialEstadoPedido> historialEstadosPedido = pedidoService.getHistorialEstadosByPedidoId(pedido.getId());
            if (historialEstadosPedido != null) {
                pedido.setHistorialEstadoPedido(historialEstadosPedido);
            }
        }else{
            List<HistorialEstadoPedido> historialInicial = new ArrayList<>();
            historialInicial.add(new HistorialEstadoPedido(pedido, EstadoPedido.INGRESADO, EstadoPedido.PENDIENTE, new Timestamp(System.currentTimeMillis())));
            pedido.setHistorialEstadoPedido(historialInicial);
        }
        // recibimos el objeto pedido del formulario y lo persistimos
        pedidoService.save(pedido);
        flashmsg.addFlashAttribute("success", "Listado de pedidos actualizado");

        // redirigimos al listado
        return "redirect:/pedidos/listar";
    }

    @GetMapping(value = "/pedidos/listar")
    public String listar(Model model){
        model.addAttribute("titulo", "Listado de pedidos");

        List<Pedido> pedidos = pedidoService.findAllByOrderByPrioridadDesc();
        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("pedidos", pedidos);

        return "pedidos/pedidos";
    }

    @GetMapping(value = "/pedidos/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Pedido pedido = pedidoService.findById(id);

        // chequeamos que se pase un id valido
        if (id > 0 && pedido != null) {
            // obtenemos el pedido desde la bdd por id y lo pasamos a la vista
            model.addAttribute("pedido", pedido);

            List<Cliente> clientes = clienteService.findAll();
            List<TipoPedido> tiposPedido = tipoPedidoService.findAll();

            model.addAttribute("clientes", clientes);
            model.addAttribute("tiposPedido", tiposPedido);
            model.addAttribute("titulo", "Editar pedido");
            return "pedidos/pedido";
        } else {
            flashmsg.addFlashAttribute("danger", "No se encontró el pedido.");
            return "redirect:/pedidos";
        }

    }

    @GetMapping("/pedidos/eliminar/{id}")
    public String eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Pedido pedido = pedidoService.findById(id);
        if (pedido != null) {
            pedidoService.delete(id);
            flashmsg.addFlashAttribute("success", "Pedido eliminado correctamente.");
        }else{
            flashmsg.addFlashAttribute("danger", "Error al eliminar. No se encontró el pedido.");
        }

        return "redirect:/pedidos";
    }

    @PostMapping(value = "/pedidos/{idPedido}/maquina/{numeroMaquina}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> asignarPedidoAMaquina(@PathVariable(name = "idPedido") Long idPedido, @PathVariable(name = "numeroMaquina") Integer numeroMaquina){
        if(!pedidoService.asignarAMaquina(idPedido, numeroMaquina)){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al asignar el pedido a la máquina. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(!this.productoService.setUso(idPedido, numeroMaquina)){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al actualizar el contenido de producto usado. Revise el inventario para corregir inconsistencias.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"Pedido asignado a la máquina correctamente\"}", HttpStatus.OK);
    }

    @PostMapping(value = "/pedidos/{idPedido}/estado/{estado}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> asignarPedidoAMaquina(@PathVariable(name = "idPedido") Long idPedido, @PathVariable(name = "estado") String estado){
        if(!pedidoService.asignarAMaquina(idPedido, 0, estado)){
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al cambiar el estado del pedido. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El estado del pedido ha sido cambiado correctamente.\"}", HttpStatus.OK);
    }
}
