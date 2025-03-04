package com.burbujas.gestionlimpia.controllers;

import com.burbujas.gestionlimpia.models.entities.TipoPedido;
import com.burbujas.gestionlimpia.models.services.IPedidoService;
import com.burbujas.gestionlimpia.models.services.ITipoPedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tiposPedidos")
public class TipoPedidoController {
    private final ITipoPedidoService tipoPedidoService;
    private final IPedidoService pedidoService;

    public TipoPedidoController(ITipoPedidoService tipoPedidoService, IPedidoService pedidoService) {
        this.tipoPedidoService = tipoPedidoService;
        this.pedidoService = pedidoService;
    }

    @GetMapping(value = "/listar")
    public ResponseEntity<Object> getAllTipoPedidos() {
        List<TipoPedido> tipoPedidos = this.tipoPedidoService.findAll();
        if (!tipoPedidos.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("tipoPedidos", tipoPedidos);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("msg", "Actualmente no existen tipos de pedido.");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/{idTipoPedido}")
    public ResponseEntity<Object> getTipoPedido(@PathVariable("idTipoPedido") Long idTipoPedido) {
        TipoPedido tipoPedido = this.tipoPedidoService.findById(idTipoPedido);
        if(tipoPedido != null){
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("tipoPedido", tipoPedido);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            return new ResponseEntity<>("{\"status\":\"ERROR\",\"msg\": \"Error: no se encontró el tipo de pedido.\"}", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/nuevo", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createTipoPedido(@RequestBody TipoPedido tipoPedido) {
        try {
            TipoPedido nuevoTipoPedido = this.tipoPedidoService.save(tipoPedido);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("nuevoTipoPedido", nuevoTipoPedido);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("msg", "Ha ocurrido un error al crear el tipo de pedido. Intente nuevamente.");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/editar/{idTipoPedido}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateTipoPedido(@PathVariable("idTipoPedido") Long idTipoPedido, @RequestBody TipoPedido tipoPedido) {
        try {
            TipoPedido existingTipoPedido = this.tipoPedidoService.findById(idTipoPedido);
            if (existingTipoPedido == null) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("status", "ERROR");
                errorResponse.put("msg", "Error: no se encontró el tipo de pedido.");
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }

            existingTipoPedido.setDescripcion(tipoPedido.getDescripcion());
            existingTipoPedido.setMinutosDuracionLavado(tipoPedido.getMinutosDuracionLavado());
            existingTipoPedido.setMinutosDuracionSecado(tipoPedido.getMinutosDuracionSecado());

            this.tipoPedidoService.save(existingTipoPedido);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("msg", "El tipo de pedido ha sido actualizado correctamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "ERROR");
            errorResponse.put("msg", "Error al editar el tipo de pedido. Intente nuevamente");
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(value = "/eliminar/{idTipoPedido}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> eliminarTipoPedido(@PathVariable("idTipoPedido") Long idTipoPedido) {
        try{
            if(!this.pedidoService.findAllByTipoPedido(this.tipoPedidoService.findById(idTipoPedido)).isEmpty()){
                return new ResponseEntity<>("{\"status\":\"ERROR\",\"msg\": \"No se pudo eliminar el tipo de pedido debido a que existen pedidos con este tipo asociado.\"}", HttpStatus.CONFLICT);
            }else{
                this.tipoPedidoService.delete(idTipoPedido);
            }
        }catch (Exception e){
            System.out.printf("Excepción capturada al eliminar un tipo de pedido: "+e);
            return new ResponseEntity<>("{\"status\":\"ERROR\",\"msg\": \"Error al eliminar el tipo de pedido. Intente nuevamente.\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("{\"status\":\"SUCCESS\",\"msg\": \"Tipo de pedido eliminado correctamente.\"}", HttpStatus.OK);
    }

}