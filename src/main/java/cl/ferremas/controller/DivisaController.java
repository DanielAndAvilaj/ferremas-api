package cl.ferremas.controller;

import cl.ferremas.external.TipoCambioService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/divisa")
public class DivisaController {

    private final TipoCambioService tipoCambioService;

    public DivisaController(TipoCambioService tipoCambioService) {
        this.tipoCambioService = tipoCambioService;
    }

    @GetMapping("/dolar")
    public Double obtenerValorDolar() {
        return tipoCambioService.obtenerValorDolar();
    }

    @GetMapping("/convertir")
    public Double convertirAPesos(@RequestParam Double monto) {
        return tipoCambioService.convertirADolares(monto);
    }
}
