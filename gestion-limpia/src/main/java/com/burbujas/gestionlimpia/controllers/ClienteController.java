package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.entities.Pedido;
import com.burbujas.gestionlimpia.models.services.IClienteService;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final IClienteService clienteService;

    @Autowired
    public ClienteController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping(value = "/nuevo")
    public String crear(Model model) {
        model.addAttribute("titulo", "Nuevo cliente");

        // instanciamos un cliente
        Cliente cliente = new Cliente();

        // mapeamos el cliente al formulario
        model.addAttribute("cliente", cliente);
        return "clientes/cliente";
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "clientes/cliente";
        }

        // recibimos el objeto cliente del formulario y lo persistimos
        this.clienteService.save(cliente);

        flashmsg.addFlashAttribute("messageType", "success");
        flashmsg.addFlashAttribute("message", "Listado de clientes actualizado");

        // redirigimos al listado
        return "redirect:/clientes";
    }

    @GetMapping(value = {"/listar", "/", "" })
    public String listar(Model model){
        model.addAttribute("titulo", "Listado de clientes");
        model.addAttribute("mostrarEliminados", false);

        List<Cliente> clientes = this.clienteService.findAll();

        // pasamos los clientes obtenida a la vista
        model.addAttribute("clientes", clientes);

        return "clientes/clientes";
    }

    @GetMapping(value = "/eliminados")
    public String listarEliminados(Model model){
        model.addAttribute("titulo", "Clientes eliminados");
        model.addAttribute("mostrarEliminados", true);

        List<Cliente> clientes = this.clienteService.findAllEliminados();
        model.addAttribute("clientes", clientes);

        return "clientes/clientes";
    }

    @GetMapping(value = "/editar/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Cliente cliente = this.clienteService.findById(id);

        // chequeamos que se pase un id valido
        if (id > 0 && cliente != null) {
            // obtenemos el usuario desde la bdd por id y lo pasamos a la vista
            model.addAttribute("cliente", cliente);
            model.addAttribute("titulo", "Editar cliente");
            return "clientes/cliente";
        } else {
            flashmsg.addFlashAttribute("messageType", "error");
            flashmsg.addFlashAttribute("message", "No se encontró el cliente.");
            return "redirect:/clientes";
        }

    }

    @PostMapping("/eliminar/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable(name = "id") Long id){
        Cliente cliente = clienteService.findById(id);
        if (cliente != null) {
            this.clienteService.delete(id);
            return new ResponseEntity<Object>("{\"status\":\"OK\",\"msg\": \"El cliente ha sido eliminado correctamente.\"}", HttpStatus.OK);
        }else{
            return new ResponseEntity<Object>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar. No se encontró el cliente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/restaurar/{id}")
    public ResponseEntity<Object> restaurar(@PathVariable(name = "id") Long id) {
        try {
            this.clienteService.restaurarCliente(id);
            return new ResponseEntity<>(
                    "{\"status\":\"OK\",\"msg\": \"El cliente ha sido restaurado correctamente.\"}",
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    "{\"status\":\"ERROR\",\"msg\": \"Error al restaurar el cliente. " + e.getMessage() + "\"}",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
