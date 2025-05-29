package cl.ferremas.controller;

import cl.ferremas.model.Producto;
import cl.ferremas.service.ProductoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    @GetMapping("/codigo/{codigo}")
    public Producto buscarPorCodigo(@PathVariable String codigo) {
        return productoService.buscarPorCodigo(codigo);
    }

    @GetMapping("/nombre")
    public List<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }

    @GetMapping("/categoria")
    public List<Producto> buscarPorCategoria(@RequestParam String categoria) {
        return productoService.buscarPorCategoria(categoria);
    }

    @GetMapping("/stock")
    public List<Producto> buscarPorStockMenorA(@RequestParam("max") Integer maxStock) {
        return productoService.buscarPorStockMenorA(maxStock);
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.guardarProducto(producto);
    }
}
