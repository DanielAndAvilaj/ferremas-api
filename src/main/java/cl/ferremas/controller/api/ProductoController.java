package cl.ferremas.controller.api;

import cl.ferremas.model.Producto;
import cl.ferremas.service.ProductoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping("/batch")
    public List<Producto> crearProductos(@RequestBody List<Producto> productos) {
        return productos.stream()
            .map(productoService::guardarProducto)
            .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Long id) {
        try {
            productoService.eliminarProducto(id);
            return ResponseEntity.ok("Producto con ID " + id + " eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        producto.setId(id);
        return productoService.guardarProducto(producto);
    }

    // Endpoint para búsqueda avanzada del catálogo
    @GetMapping("/catalogo/buscar")
    public List<Producto> buscarCatalogo(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Long sucursal,
            @RequestParam(required = false) Integer precioMin,
            @RequestParam(required = false) Integer precioMax,
            @RequestParam(required = false) Boolean stock,
            @RequestParam(required = false) String q,
            @RequestParam(required = false, defaultValue = "1") Integer page
    ) {
        // Lógica de filtrado básica (puedes mejorarla según tu servicio y modelo)
        List<Producto> productos = productoService.obtenerTodos();
        if (categoria != null && !categoria.isEmpty()) {
            productos = productos.stream().filter(p -> categoria.equalsIgnoreCase(p.getCategoria())).toList();
        }
        if (marca != null && !marca.isEmpty()) {
            productos = productos.stream().filter(p -> marca.equalsIgnoreCase(p.getMarca())).toList();
        }
        if (sucursal != null) {
            productos = productos.stream().filter(p -> p.getStock() > 0).toList(); // Simplificado: stock global
        }
        if (precioMin != null) {
            productos = productos.stream().filter(p -> p.getPrecio() >= precioMin).toList();
        }
        if (precioMax != null) {
            productos = productos.stream().filter(p -> p.getPrecio() <= precioMax).toList();
        }
        if (stock != null && stock) {
            productos = productos.stream().filter(p -> p.getStock() > 0).toList();
        }
        if (q != null && !q.isEmpty()) {
            String qLower = q.toLowerCase();
            productos = productos.stream().filter(p ->
                (p.getNombre() != null && p.getNombre().toLowerCase().contains(qLower)) ||
                (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(qLower))
            ).toList();
        }
        // Paginación simple (20 por página)
        int pageSize = 20;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, productos.size());
        if (fromIndex > productos.size()) return List.of();
        return productos.subList(fromIndex, toIndex);
    }
}