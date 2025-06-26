package cl.ferremas.controller;

import cl.ferremas.model.Precio;
import cl.ferremas.model.Producto;
import cl.ferremas.service.PrecioService;
import cl.ferremas.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import cl.ferremas.dto.PrecioRequest;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final PrecioService precioService;
    private final ProductoService productoService;
    private final DecimalFormat formatoPesos = new DecimalFormat("#,###");

    public PrecioController(PrecioService precioService, ProductoService productoService) {
        this.precioService = precioService;
        this.productoService = productoService;
    }

    @GetMapping("/{codigoProducto}")
    public ResponseEntity<String> obtenerPrecios(@PathVariable String codigoProducto) {
        try {
            List<Precio> precios = precioService.obtenerPreciosPorCodigoProducto(codigoProducto);
            Producto producto = productoService.buscarPorCodigo(codigoProducto);
            
            if (producto == null) {
                return ResponseEntity.ok("❌ No se encontró ningún producto con código: " + codigoProducto);
            }
            
            if (precios.isEmpty()) {
                return ResponseEntity.ok(String.format(
                    "⚠️ Consulta de Precios\n" +
                    "🏷️ Producto: %s\n" +
                    "🏢 Marca: %s\n" +
                    "📋 Código: %s\n" +
                    "📊 Estado: Sin precios registrados\n" +
                    "📦 Stock: %d unidades",
                    producto.getNombre(),
                    producto.getMarca() != null ? producto.getMarca() : "No especificada",
                    producto.getCodigo(),
                    producto.getStock()
                ));
            }

            StringBuilder mensaje = new StringBuilder();
            mensaje.append("💰 Historial de Precios\n");
            mensaje.append("🏷️ Producto: ").append(producto.getNombre()).append("\n");
            mensaje.append("🏢 Marca: ").append(producto.getMarca() != null ? producto.getMarca() : "No especificada").append("\n");
            mensaje.append("📋 Código: ").append(producto.getCodigo()).append("\n");
            mensaje.append("🏪 Categoría: ").append(producto.getCategoria()).append("\n");
            mensaje.append("📦 Stock: ").append(producto.getStock()).append(" unidades\n");
            mensaje.append("📊 Total de precios registrados: ").append(precios.size()).append("\n\n");
            
            mensaje.append("📋 Detalle de Precios:\n");
            for (int i = 0; i < precios.size(); i++) {
                Precio precio = precios.get(i);
                String fechaFormateada = precio.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                mensaje.append(String.format("   %d. 📅 %s - 💵 $%s CLP\n", 
                    i + 1, 
                    fechaFormateada, 
                    formatoPesos.format(precio.getValor())
                ));
            }
            
            // Mostrar precio actual (más reciente)
            Precio precioActual = precios.get(0);
            mensaje.append(String.format("\n✨ Precio Vigente: $%s CLP (%s)", 
                formatoPesos.format(precioActual.getValor()),
                precioActual.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));

            return ResponseEntity.ok(mensaje.toString());
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error al consultar los precios del producto: " + codigoProducto);
        }
    }

    @PostMapping
    public ResponseEntity<String> agregarPrecio(@RequestBody PrecioRequest precioRequest) {
        try {
            // Buscar el producto
            Producto producto = productoService.obtenerPorId(precioRequest.getProductoId());
            
            // Crear el precio
            Precio precio = new Precio();
            precio.setFecha(precioRequest.getFecha());
            precio.setValor(precioRequest.getValor());
            precio.setProducto(producto);
            
            Precio precioGuardado = precioService.guardarPrecio(precio);
            
            String fechaFormateada = precioGuardado.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            String mensaje = String.format(
                "✅ Precio Registrado Exitosamente\n" +
                "🏷️ Producto: %s\n" +
                "🏢 Marca: %s\n" +
                "📋 Código: %s\n" +
                "💵 Nuevo Precio: $%s CLP\n" +
                "📅 Fecha de Vigencia: %s\n" +
                "🆔 ID del Precio: %d",
                producto.getNombre(),
                producto.getMarca() != null ? producto.getMarca() : "No especificada",
                producto.getCodigo(),
                formatoPesos.format(precioGuardado.getValor()),
                fechaFormateada,
                precioGuardado.getId()
            );

            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error al registrar el precio. Verifique que el producto existe y los datos sean correctos.");
        }
    }
}