package com.burbujas.gestionlimpia.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import java.util.Map;

@Controller
public class CustomErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.STACK_TRACE)
        );
        int statusCode = (int) errorDetails.getOrDefault("status", 500);
        String errorMessage = (String) errorDetails.getOrDefault("error", "Error desconocido");
        String stackTrace = (String) errorDetails.getOrDefault("trace", "Sin detalles disponibles");

        // vista particular para 404
        if (statusCode == 404) {
            return "error/404"; // Redirige a la vista 404
        }

        // vista general para otros errores
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("stackTrace", stackTrace);

        return "error/default";
    }

    public String getErrorPath() {
        return "/error"; // Define el path estándar para errores
    }

}