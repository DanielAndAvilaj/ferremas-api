package cl.ferremas.controller;

import cl.ferremas.model.StockSucursal;
import cl.ferremas.service.StockSucursalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-sucursales")
public class StockSucursalController {

    private final StockSucursalService stockSucursalService;

    public StockSucursalController(StockSucursalService stockSucursalService) {
        this.stockSucursalService = stockSucursalService;
    }

    @GetMapping
    public List<StockSucursal> obtenerTodos() {
        return stockSucursalService.obtenerTodos();
    }

    @GetMapping("/sucursal/{id}")
    public List<StockSucursal> buscarPorSucursal(@PathVariable Long id) {
        return stockSucursalService.buscarPorSucursal(id);
    }

    @GetMapping("/producto/{id}")
    public List<StockSucursal> buscarPorProducto(@PathVariable Long id) {
        return stockSucursalService.buscarPorProducto(id);
    }

    @PostMapping
    public ResponseEntity<StockSucursal> crear(@RequestBody StockSucursal stockSucursal) {
        return ResponseEntity.ok(stockSucursalService.guardar(stockSucursal));
    }
}
