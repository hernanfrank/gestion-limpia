package com.burbujas.gestionlimpia.controllers;


import com.burbujas.gestionlimpia.models.entities.Config;
import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.services.*;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Controller
@ControllerAdvice
@RequestMapping("/administracion")
public class ConfigController {

    private final IConfigService configService;
    private final ITipoPedidoService tipoPedidoService;
    private final IAlertaReabastecimientoService alertaReabastecimientoService;
    private final IDatabaseBackupService databaseBackupService;
    private final IClaveResetService claveResetService;

    public ConfigController(IConfigService configService, ITipoPedidoService tipoPedidoService, IAlertaReabastecimientoService alertaReabastecimientoService, IDatabaseBackupService databaseBackupService, IClaveResetService claveResetService) {
        this.configService = configService;
        this.tipoPedidoService = tipoPedidoService;
        this.alertaReabastecimientoService = alertaReabastecimientoService;
        this.databaseBackupService = databaseBackupService;
        this.claveResetService = claveResetService;
    }

    @ModelAttribute
    // este método se inyecta antes de cada respuesta a petición http para devolver el nombre y logo de la lavandería a todas las vistas
    public void addGlobalAttributes(Model model) {
        Config config = this.configService.findById(1L);
        model.addAttribute("config", config);
        if (config != null) {
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
        if (config == null) {
            Config emptyConfig = new Config();
            model.addAttribute("config", emptyConfig);
        } else {
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
        try {
            if (logo.getBytes().length > 0) {
                //casteamos el logo de MultipartFile a un array de bytes
                byte[] byteArr = logo.getBytes();
                InputStream logoInputStream = new ByteArrayInputStream(byteArr);
                config.setLogoLavanderia(logoInputStream.readAllBytes());
            }
            BindingResult auxValidation = new BeanPropertyBindingResult(config, "config");
            result.getFieldErrors().forEach(fieldError -> {
                if (!fieldError.getField().equals("logoLavanderia")) {
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
            if (config.getTimeoutAlertaRabastecimiento() != 0) {
                alertaReabastecimientoService.programarAlerta();
            } else {
                // si el tiempo entre alertas se deja en 0, se desactiva
                alertaReabastecimientoService.cancelarYEliminar();
            }

            flashmsg.addFlashAttribute("messageType", "success");
            flashmsg.addFlashAttribute("message", "Configuración actualizada correctamente");

            // redirigimos a la view
            return "redirect:/administracion";
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar la configuración: " + e);
        }
    }

    @GetMapping("/backup/crear")
    public ResponseEntity<?> crearBackup() {
        try {
            Map<Integer, String> processStatus = databaseBackupService.crearBackup();
            if (processStatus.keySet().toArray()[0].equals(0)) {// si se pudo crear el archivo, lo mandamos a descargar
                // nombre del archivo
                String fileName = processStatus.get(0);
                File file = new File(fileName);

                if (file.exists()) {
                    // crear el recurso para descargar
                    InputStreamResource recurso = new InputStreamResource(new FileInputStream(file));

                    ResponseEntity<InputStreamResource> response = ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                            .contentType(MediaType.parseMediaType("application/sql"))
                            .contentLength(file.length())
                            .body(recurso);

                    //elimina el archivo temporal
                    databaseBackupService.deleteTempFile(file.getName());

                    return response;
                } else {
                    System.out.println("ConfigController: Excepción capturada al generar la copia de seguridad. No se encontró el archivo temporal.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo generar la copia de seguridad.");
                }
            } else {
                System.out.println("ConfigController: Error al ejecutar el proceso para generar la copia de seguridad. Código de error: " + processStatus.keySet().toArray()[0]);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo generar la copia de seguridad.");
            }
        } catch (Exception e) {
            System.out.println("ConfigController: Excepción capturada al generar la copia de seguridad: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo generar la copia de seguridad.");
        }
    }

    @PostMapping("/backup/restaurar")
    public ResponseEntity<?> restoreDatabase(@RequestParam("archivoBackup") MultipartFile archivoBackup, @RequestParam("claveAccesoRestaurar") String claveAcceso) {
        try {
            if(!this.configService.verificarClaveAcceso(claveAcceso)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La clave de acceso no es correcta.");
            }
            int resultCode = databaseBackupService.restaurarBackup(archivoBackup);
            databaseBackupService.deleteTempFile(archivoBackup.getOriginalFilename());
            if (resultCode == 0) {
                return ResponseEntity.ok("La base de datos fue restaurada correctamente.");
            } else {
                System.out.println("ConfigController: Error al restaurar la base de datos. Código de salida: " + resultCode);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restaurar la base de datos.");
            }

        } catch (Exception e) {
            System.out.println("ConfigController: Error al restaurar la base de datos: " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al restaurar la base de datos.");
        }
    }

    @PostMapping("/clave/cambiar")
    public ResponseEntity<?> cambiarClave(@RequestParam("claveActual") String claveActual,
                                          @RequestParam("claveNueva") String claveNueva) {
        try {
            int result = this.claveResetService.cambiarClave(claveActual, claveNueva);
            if(result == 0){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: no se encontró la configuración del sistema.");
            }else if (result == 2){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("La clave actual no es correcta.");
            }else if(result == 3){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La clave debe contener al menos una letra mayúscula, una letra minúscula y un número.");
            }
        } catch (Exception e) {
            System.out.println("ConfigController: Excepción capturada al intentar cambio de clave " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al cambiar la clave. Intente nuevamente.");
        }
        return ResponseEntity.ok("La clave se cambió correctamente.");
    }

    @PostMapping("/clave/cambiar/enviarMail")
    public ResponseEntity<?> solicitarCambioClave(@RequestParam String email) {
        try {
            this.claveResetService.solicitarCambioClave(email);
            return ResponseEntity.ok("En los próximos minutos recibirá un email con las instrucciones para cambiar su clave");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/clave/cambiar/{token}")
    public String formularioCambiarClave(@PathVariable("token") String token, Model model) {
        Config config = this.configService.findById(1L);
        if (config != null) {
            model.addAttribute("nombreLavanderia", config.getNombreLavanderia());
            byte[] encodeBase64 = Base64.getEncoder().encode(config.getLogoLavanderia());
            String base64Encoded = new String(encodeBase64, StandardCharsets.UTF_8);
            model.addAttribute("logoLavanderia", base64Encoded);
        }
        model.addAttribute("token", token);
        return "cambiarClave";
    }

    @PostMapping("/clave/cambiar/desdeMail")
    public ResponseEntity<?> cambiarClaveConToken(@RequestParam String token, @RequestParam String nuevaClave) {
        try {
            int result = this.claveResetService.cambiarClaveConToken(token, nuevaClave);
            if(result == 0){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: no se encontró la configuración del sistema.");
            }else if (result == 2){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ha ocurrido un error en el proceso. Intente enviando el correo de recuperación nuevamente.");
            }else if(result == 3){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La clave debe contener al menos una letra mayúscula, una letra minúscula y un número.");
            }
            return ResponseEntity.ok("La clave ha sido actualizada correctamente");
        } catch (Exception e) {
            System.out.println("ConfigController: Excepción capturada al intentar cambio de clave con token " + e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/email/cambiar")
    public ResponseEntity<?> cambiarEmail(@RequestParam("emailActual") String emailActual,
                                          @RequestParam("emailNuevo") String emailNuevo) {
        try {
            int result = this.configService.cambiarEmail(emailActual, emailNuevo);
            if(result == 0){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: no se encontró la configuración del sistema.");
            }else if (result == 2){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El email actual es incorrecto.");
            } else if (result == 3) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email de acceso debe seguir el formato mi@email.com");
            } else if (result == 4) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El email nuevo ingresado es el mismo que el email actual.");
            }
        } catch (Exception e) {
            System.out.println("ConfigController: Excepción capturada al intentar cambio de email " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error al cambiar el email. Intente nuevamente.");
        }
        return ResponseEntity.ok("El email se cambió correctamente.");
    }

}
