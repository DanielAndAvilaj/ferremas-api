package cl.ferremas.controller.api;

import cl.ferremas.model.CarritoItem;
import cl.ferremas.service.CarritoService;
import cl.ferremas.service.PagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {
    @Autowired
    private CarritoService carritoService;
    @Autowired
    private PagoService pagoService;

    @PostMapping("/agregar")
    public ResponseEntity<?> agregar(@RequestParam String sessionId, @RequestParam Long productoId, @RequestParam int cantidad) {
        System.out.println("🔍 DEBUG: CarritoController.agregar - SessionId: " + sessionId + ", ProductoId: " + productoId + ", Cantidad: " + cantidad);
        try {
            CarritoItem item = carritoService.agregarProducto(sessionId, productoId, cantidad);
            System.out.println("🔍 DEBUG: CarritoController.agregar - Item agregado exitosamente: " + item.getId());
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            System.out.println("🔍 DEBUG: CarritoController.agregar - Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/items")
    public List<CarritoItem> items(@RequestParam String sessionId) {
        System.out.println("🔍 DEBUG: CarritoController.items - SessionId: " + sessionId);
        try {
            List<CarritoItem> items = carritoService.obtenerItems(sessionId);
            System.out.println("🔍 DEBUG: CarritoController.items - Items encontrados: " + items.size());
            
            // Log detallado de cada item
            items.forEach(item -> {
                System.out.println("🔍 DEBUG: CarritoController.items - Item ID: " + item.getId() + 
                                 ", Tipo ID: " + (item.getId() != null ? item.getId().getClass().getSimpleName() : "null") +
                                 ", Producto: " + (item.getProducto() != null ? item.getProducto().getNombre() : "null") +
                                 ", Cantidad: " + item.getCantidad());
            });
            
            return items;
        } catch (Exception e) {
            System.out.println("🔍 DEBUG: CarritoController.items - Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @PutMapping("/item/{id}")
    public ResponseEntity<?> actualizarCantidad(@PathVariable Long id, @RequestParam String sessionId, @RequestParam int cantidad) {
        System.out.println("🔍 DEBUG: CarritoController.actualizarCantidad - ItemId: " + id + ", SessionId: " + sessionId + ", Cantidad: " + cantidad);
        try {
            CarritoItem item = carritoService.actualizarCantidad(id, sessionId, cantidad);
            System.out.println("🔍 DEBUG: CarritoController.actualizarCantidad - Item actualizado exitosamente: " + item.getId() + ", Nueva cantidad: " + item.getCantidad());
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            System.out.println("🔍 DEBUG: CarritoController.actualizarCantidad - Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id, @RequestParam String sessionId) {
        try {
            carritoService.quitarItem(id, sessionId);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestParam String sessionId, @RequestParam String emailCliente) {
        System.out.println("🔍 DEBUG: CarritoController.checkout - INICIO");
        System.out.println("🔍 DEBUG: CarritoController.checkout - SessionId: " + sessionId);
        System.out.println("🔍 DEBUG: CarritoController.checkout - EmailCliente: " + emailCliente);
        
        try {
            // Validar stock antes de iniciar pago
            System.out.println("🔍 DEBUG: CarritoController.checkout - Validando stock...");
            if (!carritoService.validarStock(sessionId)) {
                System.out.println("🔍 DEBUG: CarritoController.checkout - Stock insuficiente");
                return ResponseEntity.badRequest().body(Map.of("error", "Stock insuficiente en uno o más productos"));
            }
            
            System.out.println("🔍 DEBUG: CarritoController.checkout - Calculando total...");
            double total = carritoService.calcularTotal(sessionId);
            System.out.println("🔍 DEBUG: CarritoController.checkout - Total calculado: " + total);
            
            // Iniciar pago con PagoService (Webpay)
            System.out.println("🔍 DEBUG: CarritoController.checkout - Iniciando transacción con PagoService...");
            return pagoService.iniciarTransaccionCompleta(total, "Compra en Ferremas", emailCliente)
                    .map(res -> {
                        System.out.println("🔍 DEBUG: CarritoController.checkout - Transacción iniciada exitosamente: " + res);
                        // Limpiar carrito después de iniciar pago (opcional: limpiar tras confirmación)
                        // carritoService.limpiarCarrito(sessionId);
                        return ResponseEntity.ok(res);
                    })
                    .onErrorResume(e -> {
                        System.out.println("🔍 DEBUG: CarritoController.checkout - Error en PagoService: " + e.getMessage());
                        e.printStackTrace();
                        Map<String, Object> error = new HashMap<>();
                        error.put("error", true);
                        error.put("mensaje", e.getMessage());
                        return reactor.core.publisher.Mono.just(ResponseEntity.badRequest().body(error));
                    })
                    .block();
        } catch (Exception e) {
            System.out.println("🔍 DEBUG: CarritoController.checkout - Error general: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("error", true);
            error.put("mensaje", "Error interno del servidor: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
} 