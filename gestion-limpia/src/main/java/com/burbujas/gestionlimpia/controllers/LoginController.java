package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.services.IConfigService;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    private final IConfigService configService;
    
    @Autowired
    public LoginController(IConfigService configService) {
        this.configService = configService;
    }
    
    @GetMapping("/login")
    public String login(Model model) {
        Config config = this.configService.findById(1L);
        if (config != null) {
            model.addAttribute("nombreLavanderia", config.getNombreLavanderia());
            byte[] encodeBase64 = Base64.getEncoder().encode(config.getLogoLavanderia());
            String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
            model.addAttribute("logoLavanderia", base64Encoded);
        }
        return "login";
    }
}