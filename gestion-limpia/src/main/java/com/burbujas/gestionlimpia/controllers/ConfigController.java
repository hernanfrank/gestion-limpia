package com.burbujas.gestionlimpia.controllers;


import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.services.IAlertaReabastecimientoService;
import com.burbujas.gestionlimpia.models.services.IConfigService;
import com.burbujas.gestionlimpia.models.services.ITipoPedidoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@Controller
@ControllerAdvice
@RequestMapping("/administracion")
public class ConfigController {

    private final IConfigService configService;
    private final ITipoPedidoService tipoPedidoService;
    private final IAlertaReabastecimientoService alertaReabastecimientoService;

    public ConfigController(IConfigService configService, ITipoPedidoService tipoPedidoService, IAlertaReabastecimientoService alertaReabastecimientoService) {
        this.configService = configService;
        this.tipoPedidoService = tipoPedidoService;
        this.alertaReabastecimientoService = alertaReabastecimientoService;
    }

    @ModelAttribute
    // este método se inyecta antes de cada respuesta a petición http para devolver el nombre y logo de la lavandería a todas las vistas
    public void addGlobalAttributes(Model model) {
        Config config = this.configService.findById(1L);
        model.addAttribute("config", config);
        if(config != null){
            model.addAttribute("nombreLavanderia", config.getNombreLavanderia());

            byte[] encodeBase64 = Base64.getEncoder().encode(config.getLogoLavanderia());
            String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
            model.addAttribute("logoLavanderia", base64Encoded);

            model.addAttribute("entregaPedidosAutomatica", config.getEntregaPedidosAutomatica());
        }
    }

    @GetMapping(value = {"/", ""})
    public String listarConfiguraciones(Model model) throws UnsupportedEncodingException {
        model.addAttribute("titulo", "Administración del Sistema");
        Config config = this.configService.findById(1L);
        model.addAttribute("config", config);
        if(config == null){
            Config emptyConfig = new Config(null, "", new byte[0], false, 0);
            model.addAttribute("config", emptyConfig);
        }else{
            byte[] encodeBase64 = Base64.getEncoder().encode(config.getLogoLavanderia());
            String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
            model.addAttribute("logoBlob", base64Encoded);
        }
        List<TipoPedido> tipoPedidoList = this.tipoPedidoService.findAll();
        model.addAttribute("tipoPedidos", tipoPedidoList);
        return "administracion";
    }

    @PostMapping(value = "/guardar")
    public String guardar(@Valid Config config,
                          BindingResult result,
                          Model model,
                          @RequestPart(value = "logoLavanderia", required = false) MultipartFile logo,
                          RedirectAttributes flashmsg) throws IOException {
        try{
            if(logo.getBytes().length > 0){
                //casteamos el logo de MultipartFile a un array de bytes
                byte [] byteArr = logo.getBytes();
                InputStream logoInputStream = new ByteArrayInputStream(byteArr);
                config.setLogoLavanderia(logoInputStream.readAllBytes());
            }
            BindingResult auxValidation = new BeanPropertyBindingResult(config, "config");
            result.getFieldErrors().forEach(fieldError -> {
                if(!fieldError.getField().equals("logoLavanderia")){
                    auxValidation.addError(fieldError);
                }
            });

            if (auxValidation.hasErrors()) { // si tiene algún error en la validación lo mostramos en los msg del formulario
                model.addAttribute("titulo", model.getAttribute("titulo"));
                return "administracion";
            }

            // recibimos el objeto config del formulario y lo persistimos
            configService.save(config);

            // tiempo entre alertas de reabastecimiento
            if(config.getTimeoutAlertaRabastecimiento() != 0){
                alertaReabastecimientoService.programarAlerta();
            }else{
            // si el tiempo entre alertas se deja en 0, se desactiva
                alertaReabastecimientoService.cancelarYEliminar();
            }

            flashmsg.addFlashAttribute("messageType", "success");
            flashmsg.addFlashAttribute("message", "Configuración actualizada correctamente");

            // redirigimos a la view
            return "redirect:/administracion";
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la configuración: "+e);
        }
    }
}
