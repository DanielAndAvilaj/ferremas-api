package cl.ferremas.service;

import cl.ferremas.model.CarritoItem;
import cl.ferremas.model.Producto;
import cl.ferremas.repository.CarritoItemRepository;
import cl.ferremas.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la lógica de negocio del carrito de compras.
 */
@Service
public class CarritoService {
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoItemRepository carritoItemRepository, ProductoRepository productoRepository) {
        this.carritoItemRepository = carritoItemRepository;
        this.productoRepository = productoRepository;
    }

    /**
     * Obtiene los items del carrito para una sesión.
     */
    public List<CarritoItem> obtenerItems(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId);
    }

    /**
     * Agrega un producto al carrito para una sesión.
     */
    public CarritoItem agregarProducto(String sessionId, Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock() + ", Solicitado: " + cantidad);
        }
        Optional<CarritoItem> existente = carritoItemRepository.findBySessionId(sessionId).stream()
                .filter(i -> i.getProducto().getId().equals(productoId)).findFirst();
        CarritoItem item;
        if (existente.isPresent()) {
            item = existente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente para la cantidad total. Disponible: " + producto.getStock() + ", Total solicitado: " + nuevaCantidad);
            }
            item.setCantidad(nuevaCantidad);
        } else {
            item = new CarritoItem();
            item.setSessionId(sessionId);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(producto.getPrecio());
            item.setFechaCreacion(LocalDateTime.now());
        }
        return carritoItemRepository.save(item);
    }

    /**
     * Quita un item del carrito por ID y sesión.
     */
    public void quitarItem(Long itemId, String sessionId) {
        CarritoItem item = carritoItemRepository.findById(itemId).orElseThrow();
        if (!item.getSessionId().equals(sessionId)) throw new RuntimeException("No autorizado");
        carritoItemRepository.deleteById(itemId);
    }

    /**
     * Actualiza la cantidad de un item del carrito.
     */
    public CarritoItem actualizarCantidad(Long itemId, String sessionId, int cantidad) {
        System.out.println("🔍 DEBUG: CarritoService.actualizarCantidad - ItemId: " + itemId + ", SessionId: " + sessionId + ", Cantidad: " + cantidad);
        
        if (cantidad <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }
        
        CarritoItem item = carritoItemRepository.findById(itemId)
            .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado con ID: " + itemId));
        
        System.out.println("🔍 DEBUG: CarritoService.actualizarCantidad - Item encontrado: " + item.getId() + ", SessionId del item: " + item.getSessionId());
        
        if (!item.getSessionId().equals(sessionId)) {
            throw new RuntimeException("No autorizado: El item no pertenece a esta sesión");
        }
        
        if (item.getProducto().getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + item.getProducto().getStock() + ", Solicitado: " + cantidad);
        }
        
        item.setCantidad(cantidad);
        CarritoItem itemActualizado = carritoItemRepository.save(item);
        
        System.out.println("🔍 DEBUG: CarritoService.actualizarCantidad - Item actualizado exitosamente: " + itemActualizado.getId() + ", Nueva cantidad: " + itemActualizado.getCantidad());
        
        return itemActualizado;
    }

    /**
     * Calcula el total del carrito para una sesión.
     */
    public double calcularTotal(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId).stream()
                .mapToDouble(i -> i.getPrecioUnitario() * i.getCantidad()).sum();
    }

    /**
     * Valida que todos los items del carrito tengan stock suficiente.
     */
    public boolean validarStock(String sessionId) {
        return carritoItemRepository.findBySessionId(sessionId).stream()
                .allMatch(i -> i.getProducto().getStock() >= i.getCantidad());
    }

    /**
     * Limpia el carrito de una sesión.
     */
    @Transactional
    public void limpiarCarrito(String sessionId) {
        carritoItemRepository.deleteBySessionId(sessionId);
    }
} 