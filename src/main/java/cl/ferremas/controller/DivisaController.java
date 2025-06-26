package cl.ferremas.controller;

import cl.ferremas.external.TipoCambioService;
import cl.ferremas.model.Producto;
import cl.ferremas.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/divisa")
public class DivisaController {

    private final TipoCambioService tipoCambioService;
    private final ProductoService productoService;
    private final DecimalFormat formatoPesos = new DecimalFormat("#,###");
    private final DecimalFormat formatoDolares = new DecimalFormat("#,##0.00");

    public DivisaController(TipoCambioService tipoCambioService, ProductoService productoService) {
        this.tipoCambioService = tipoCambioService;
        this.productoService = productoService;
    }

    @GetMapping("/dolar")
    public Double obtenerValorDolar() {
        return tipoCambioService.obtenerValorDolar();
    }

    @GetMapping("/convertir")
    public Double convertirAPesos(@RequestParam Double monto) {
        return tipoCambioService.convertirADolares(monto);
    }

    @GetMapping("/producto/{id}/precio-dolares")
    public ResponseEntity<String> obtenerPrecioProductoEnDolares(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            
            if (producto.getPrecios() == null || producto.getPrecios().isEmpty()) {
                return ResponseEntity.ok("❌ El producto '" + producto.getNombre() + "' no tiene precios registrados.");
            }

            // Obtener el precio más reciente
            Double precioEnPesos = producto.getPrecios().get(0).getValor();
            Double valorDolar = tipoCambioService.obtenerValorDolar();
            Double precioEnDolares = precioEnPesos / valorDolar;
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            String mensaje = String.format(
                "🛍️ Consulta de Precio en Dólares\n" +
                "📅 Fecha: %s\n" +
                "🏷️ Producto: %s\n" +
                "🏢 Marca: %s\n" +
                "📋 Código: %s\n" +
                "💵 Precio en Pesos: $%s CLP\n" +
                "💲 Precio en Dólares: USD $%s\n" +
                "📊 Tipo de cambio: $%s CLP por USD\n" +
                "📦 Stock disponible: %d unidades\n",
                fechaActual,
                producto.getNombre(),
                producto.getMarca() != null ? producto.getMarca() : "No especificada",
                producto.getCodigo() != null ? producto.getCodigo() : "No especificado",
                formatoPesos.format(precioEnPesos),
                formatoDolares.format(precioEnDolares),
                formatoPesos.format(valorDolar),
                producto.getStock()
            );

            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ No se encontró el producto o error al consultar el tipo de cambio.");
        }
    }

    @GetMapping("/convertir-precio")
    public ResponseEntity<String> convertirPrecioPersonalizado(
            @RequestParam Double precio,
            @RequestParam(required = false) String nombreProducto) {
        try {
            Double valorDolar = tipoCambioService.obtenerValorDolar();
            Double precioEnDolares = precio / valorDolar;
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String producto = nombreProducto != null ? nombreProducto : "Producto personalizado";
            
            String mensaje = String.format(
                "💰 Conversión de Precio Personalizada\n" +
                "📅 Fecha: %s\n" +
                "🏷️ Producto: %s\n" +
                "💵 Precio en Pesos: $%s CLP\n" +
                "💲 Precio en Dólares: USD $%s\n" +
                "📊 Tipo de cambio: $%s CLP por USD\n" +
                "ℹ️  Fuente: Mindicador.cl (Banco Central de Chile)",
                fechaActual,
                producto,
                formatoPesos.format(precio),
                formatoDolares.format(precioEnDolares),
                formatoPesos.format(valorDolar)
            );

            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error al obtener el tipo de cambio. Intente nuevamente.");
        }
    }

    @GetMapping("/tipo-cambio-actual")
    public ResponseEntity<String> obtenerTipoCambioActual() {
        try {
            Double valorDolar = tipoCambioService.obtenerValorDolar();
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
            String mensaje = String.format(
                "📊 Tipo de Cambio Actual\n" +
                "📅 Fecha y hora: %s\n" +
                "💲 1 USD = $%s CLP\n" +
                "💵 1 CLP = USD $%s\n" +
                "ℹ️  Fuente: Mindicador.cl (Banco Central de Chile)\n" +
                "🔄 Actualización: Diaria",
                fechaActual,
                formatoPesos.format(valorDolar),
                formatoDolares.format(1/valorDolar)
            );

            return ResponseEntity.ok(mensaje);
            
        } catch (Exception e) {
            return ResponseEntity.ok("❌ Error al obtener el tipo de cambio. Intente nuevamente.");
        }
    }
}