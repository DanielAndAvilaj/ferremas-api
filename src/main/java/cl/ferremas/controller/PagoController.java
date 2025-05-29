package cl.ferremas.controller;

import cl.ferremas.service.PagoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/pago")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    /**
     * Inicia una transacción en Webpay Plus
     * @param body JSON con el monto a pagar (ej: {"monto": 1000})
     * @return Respuesta con URL y token
     */
    @PostMapping("/iniciar")
    public Mono<ResponseEntity<Map<String, Object>>> iniciarPago(@RequestBody Map<String, Object> body) {
        Double monto = Double.valueOf(body.get("monto").toString());
        return pagoService.iniciarTransaccion(monto)
                .map(ResponseEntity::ok);
    }

    /**
     * Confirma una transacción luego de retorno desde Webpay (GET)
     * @param token Token entregado por Webpay (token_ws)
     * @return Resultado de la transacción
     */
    @GetMapping("/confirmar")
    public Mono<ResponseEntity<Map<String, Object>>> confirmarPagoGet(@RequestParam("token_ws") String token) {
        return pagoService.confirmarTransaccion(token)
                .map(ResponseEntity::ok);
    }

    /**
     * Confirma una transacción luego de retorno desde Webpay (POST)
     * @param token Token entregado por Webpay (token_ws)
     * @return Resultado de la transacción
     */
    @PostMapping("/confirmar")
    public Mono<ResponseEntity<Map<String, Object>>> confirmarPagoPost(@RequestParam("token_ws") String token) {
        return pagoService.confirmarTransaccion(token)
                .map(ResponseEntity::ok);
    }
}
