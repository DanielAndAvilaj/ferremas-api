package cl.ferremas.controller;

import cl.ferremas.model.Precio;
import cl.ferremas.service.PrecioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final PrecioService precioService;

    public PrecioController(PrecioService precioService) {
        this.precioService = precioService;
    }

    @GetMapping("/{codigoProducto}")
    public List<Precio> obtenerPrecios(@PathVariable String codigoProducto) {
        return precioService.obtenerPreciosPorCodigoProducto(codigoProducto);
    }

    @PostMapping
    public Precio agregarPrecio(@RequestBody Precio precio) {
        return precioService.guardarPrecio(precio);
    }
}
