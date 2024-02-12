package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Cliente;
import com.burbujas.gestionlimpia.models.services.IClienteService;
import com.burbujas.gestionlimpia.utils.paginators.PageRender;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "cliente";
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Cliente cliente, BindingResult result, Model model, RedirectAttributes flashmsg) {

        if (result.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
            model.addAttribute("titulo", model.getAttribute("titulo"));
            return "cliente";
        }

        // recibimos el objeto cliente del formulario y lo persistimos
        clienteService.save(cliente);

        flashmsg.addFlashAttribute("success", "Listado de clientes actualizado");

        // redirigimos al listado
        return "redirect:/clientes";
    }

    @GetMapping(value = {"/listar", "" })
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model){
        model.addAttribute("titulo", "Listado de clientes");

        // para la paginación creamos un pageable usando PageRequest
        // recibe el numero de pagina actual y el total de registros x pagina
        Pageable pageRequest = PageRequest.of(page, 4);

        // pedimos una página al clienteService
        Page<Cliente> clientes = clienteService.findAll(pageRequest);

        // creamos el paginador
        PageRender<Cliente> pageRender = new PageRender<>("/clientes", clientes);

        // pasamos la página de clientes obtenida a la vista
        model.addAttribute("clientes", clientes);
        // pasamos el paginador a la vista
        model.addAttribute("page", pageRender);
        return "clientes";
    }

    @GetMapping(value = "/{id}")
    public String editar(@PathVariable(value = "id") Long id, RedirectAttributes flashmsg, Model model) {

        Cliente cliente = clienteService.findById(id);

        // chequeamos que se pase un id valido
        if (id > 0 && cliente != null) {
            // obtenemos el usuario desde la bdd por id y lo pasamos a la vista
            model.addAttribute("cliente", cliente);
            model.addAttribute("titulo", "Editar cliente");
            return "cliente";
        } else {
            flashmsg.addFlashAttribute("error", "No se encontró el cliente.");
            return "redirect:/clientes";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable(name = "id") Long id, RedirectAttributes flashmsg){
        Cliente cliente = clienteService.findById(id);
        if (cliente != null) {
            clienteService.delete(id);
            flashmsg.addFlashAttribute("success", "Cliente eliminado correctamente.");
        }
        flashmsg.addFlashAttribute("error", "Error al eliminar. No se encontró el cliente.");

        return "redirect:/clientes";
    }
}
