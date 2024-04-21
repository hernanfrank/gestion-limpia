package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Maquina;
import com.burbujas.gestionlimpia.models.services.IMaquinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/maquinas")
public class MaquinasController {

    private final IMaquinaService maquinaService;

    @Autowired
    public MaquinasController(IMaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @GetMapping(value = "/configurar")
    public String configurar(Model model){
        List<Maquina> maquinas = maquinaService.findAll();
        // pasamos los de pedidos obtenida a la vista
        model.addAttribute("maquinas", maquinas);
        return "maquinas/configurar";
    }
}
