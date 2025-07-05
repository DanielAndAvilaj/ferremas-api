package cl.ferremas.controller.web;

import cl.ferremas.model.Producto;
import cl.ferremas.model.Precio;
import cl.ferremas.service.ProductoService;
import cl.ferremas.repository.PrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate; // ✅ CORREGIDO: LocalDate en lugar de LocalDateTime
import java.util.List;
import cl.ferremas.service.MensajeService;
import cl.ferremas.service.StockSucursalService;
import cl.ferremas.service.UsuarioService;
import cl.ferremas.model.Mensaje;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import cl.ferremas.model.Usuario;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

   @Autowired
   private ProductoService productoService;

   @Autowired
   private PrecioRepository precioRepository;

   @Autowired
   private MensajeService mensajeService;

   @Autowired
   private StockSucursalService stockSucursalService;

   @Autowired
   private UsuarioService usuarioService;

   // Dashboard principal del admin
   @GetMapping
   public String adminDashboard(Model model) {
       try {
           // Obtener usuario autenticado
           Authentication auth = SecurityContextHolder.getContext().getAuthentication();
           if (auth != null && auth.getPrincipal() instanceof Usuario) {
               model.addAttribute("usuario", (Usuario) auth.getPrincipal());
           }
           
           // Estadísticas de productos
           List<Producto> productos = productoService.obtenerTodos();
           model.addAttribute("totalProductos", productos.size());
           
           // Productos con stock bajo (< 10 unidades)
           List<Producto> productosStockBajo = productos.stream()
               .filter(p -> p.getStock() != null && p.getStock() < 10)
               .collect(Collectors.toList());
           model.addAttribute("productosStockBajo", productosStockBajo);
           model.addAttribute("productosStockBajo", productosStockBajo.size());
           
           // Estadísticas de usuarios
           long totalUsuarios = usuarioService.contarUsuariosTotal();
           model.addAttribute("totalUsuarios", totalUsuarios);
           
           // Mensajes pendientes
           long mensajesPendientes = mensajeService.contarPorEstado(Mensaje.EstadoMensaje.PENDIENTE);
           model.addAttribute("mensajesPendientes", mensajesPendientes);
           
           // Mensajes recientes (últimos 5)
           List<Mensaje> mensajesRecientes = mensajeService.obtenerPorEstado(Mensaje.EstadoMensaje.PENDIENTE)
               .stream()
               .limit(5)
               .collect(Collectors.toList());
           model.addAttribute("mensajesRecientes", mensajesRecientes);
           
           // Productos con stock bajo para mostrar en la sección reciente
           List<Producto> productosStockBajoLista = productos.stream()
               .filter(p -> p.getStock() != null && p.getStock() < 10)
               .limit(5)
               .collect(Collectors.toList());
           model.addAttribute("productosStockBajoLista", productosStockBajoLista);
           
           return "admin/dashboard";
           
       } catch (Exception e) {
           System.err.println("❌ ERROR en adminDashboard: " + e.getMessage());
           e.printStackTrace();
           model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
           
           // Valores por defecto en caso de error
           model.addAttribute("totalProductos", 0);
           model.addAttribute("productosStockBajo", 0);
           model.addAttribute("totalUsuarios", 0);
           model.addAttribute("mensajesPendientes", 0);
           model.addAttribute("productosStockBajoLista", List.of());
           model.addAttribute("mensajesRecientes", List.of());
           
           return "admin/dashboard";
       }
   }

   // ======================
   // GESTIÓN DE PRODUCTOS
   // ======================

   @GetMapping("/productos")
   public String listarProductos(Model model) {
       try {
           System.out.println("🔍 DEBUG: Iniciando listarProductos()");
           
           List<Producto> productos = productoService.obtenerTodos();
           System.out.println("🔍 DEBUG: Productos obtenidos: " + (productos != null ? productos.size() : "NULL"));
           
           // Estadísticas para el dashboard
           int totalProductos = productos.size();
           
           // Productos con stock bajo (< 10 unidades)
           long productosStockBajo = productos.stream()
               .filter(p -> p.getStock() != null && p.getStock() < 10 && p.getStock() > 0)
               .count();
           
           // Productos sin stock
           long productosSinStock = productos.stream()
               .filter(p -> p.getStock() == null || p.getStock() == 0)
               .count();
           
           // Total de sucursales (simulado por ahora)
           int totalSucursales = 3; // Centro, Norte, Sur
           
           model.addAttribute("productos", productos);
           model.addAttribute("totalProductos", totalProductos);
           model.addAttribute("productosStockBajo", productosStockBajo);
           model.addAttribute("productosSinStock", productosSinStock);
           model.addAttribute("totalSucursales", totalSucursales);
           
           System.out.println("🔍 DEBUG: Model configurado, retornando vista");
           
           return "admin/productos";
           
       } catch (Exception e) {
           System.err.println("❌ ERROR en listarProductos: " + e.getMessage());
           e.printStackTrace();
           
           model.addAttribute("error", "Error al cargar productos: " + e.getMessage());
           model.addAttribute("productos", List.of());
           model.addAttribute("totalProductos", 0);
           model.addAttribute("productosStockBajo", 0);
           model.addAttribute("productosSinStock", 0);
           model.addAttribute("totalSucursales", 0);
           
           return "admin/productos";
       }
   }

   @GetMapping("/productos/nuevo")
   public String nuevoProductoForm(Model model) {
       model.addAttribute("producto", new Producto());
       return "admin/producto-form";
   }

   // ✅ CREAR PRODUCTO - Con manejo correcto de precios
   @PostMapping("/productos")
   public String crearProducto(@ModelAttribute Producto producto, 
                              RedirectAttributes redirectAttributes,
                              Model model) {
       
       try {
           System.out.println("📝 CREAR - Producto: " + producto.getNombre() + ", Precio: " + producto.getPrecio());
           
           // Verificar código duplicado
           Producto productoExistente = productoService.buscarPorCodigo(producto.getCodigo());
           if (productoExistente != null) {
               model.addAttribute("error", "Ya existe un producto con el código: " + producto.getCodigo());
               return "admin/producto-form";
           }

           // ✅ CREAR producto usando el servicio que maneja precios
           Producto productoGuardado = productoService.crear(producto);
           
           // ✅ ASEGURAR que se cree el precio en la tabla precio
           if (producto.getPrecio() != null && producto.getPrecio() > 0) {
               crearRegistroPrecio(productoGuardado, producto.getPrecio());
           }
           
           redirectAttributes.addFlashAttribute("success", 
               "Producto '" + productoGuardado.getNombre() + "' creado exitosamente con precio $" + producto.getPrecio());
           return "redirect:/admin/productos";
           
       } catch (Exception e) {
           model.addAttribute("error", "Error al crear producto: " + e.getMessage());
           e.printStackTrace();
           return "admin/producto-form";
       }
   }

   @GetMapping("/productos/{id}/editar")
   public String editarProductoForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
       try {
           Producto producto = productoService.obtenerPorId(id);
           model.addAttribute("producto", producto);
           return "admin/producto-form";
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
           return "redirect:/admin/productos";
       }
   }

   // ✅ ACTUALIZAR PRODUCTO - Con manejo correcto de precios
   @PostMapping("/productos/{id}")
   public String actualizarProducto(@PathVariable Long id, 
                                   @ModelAttribute Producto producto, 
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
       
       try {
           System.out.println("✏️ ACTUALIZAR - ID: " + id + ", Nuevo precio: " + producto.getPrecio());
           
           // Obtener producto actual
           Producto productoActual = productoService.obtenerPorId(id);
           Double precioAnterior = productoActual.getPrecio();
           
           System.out.println("💰 Precio anterior: " + precioAnterior + " → Precio nuevo: " + producto.getPrecio());
           
           // Verificar código duplicado
           Producto productoConMismoCodigo = productoService.buscarPorCodigo(producto.getCodigo());
           if (productoConMismoCodigo != null && !productoConMismoCodigo.getId().equals(id)) {
               producto.setId(id);
               model.addAttribute("error", "Ya existe otro producto con el código: " + producto.getCodigo());
               return "admin/producto-form";
           }

           // ✅ MANEJAR CAMBIO DE PRECIO
           if (producto.getPrecio() != null && producto.getPrecio() > 0) {
               if (precioAnterior == null || !producto.getPrecio().equals(precioAnterior)) {
                   System.out.println("🔄 Precio cambiado, creando nuevo registro");
                   crearRegistroPrecio(productoActual, producto.getPrecio());
               }
           }

           // Actualizar campos del producto
           productoActual.setCodigo(producto.getCodigo());
           productoActual.setNombre(producto.getNombre());
           productoActual.setMarca(producto.getMarca());
           productoActual.setCategoria(producto.getCategoria());
           productoActual.setStock(producto.getStock());
           productoActual.setDescripcion(producto.getDescripcion());
           
           // Guardar producto actualizado
           Producto productoActualizado = productoService.actualizar(id, productoActual);

           // Si hubo cambio de precio, recargar el producto para refrescar historial y precio actual
           if (producto.getPrecio() != null && (precioAnterior == null || !producto.getPrecio().equals(precioAnterior))) {
               productoActualizado = productoService.obtenerPorId(id);
           }
           
           redirectAttributes.addFlashAttribute("success", 
               "Producto '" + productoActualizado.getNombre() + "' actualizado exitosamente. Precio: $" + producto.getPrecio());
           return "redirect:/admin/productos";
           
       } catch (Exception e) {
           producto.setId(id);
           model.addAttribute("error", "Error al actualizar producto: " + e.getMessage());
           e.printStackTrace();
           return "admin/producto-form";
       }
   }

   // ✅ MÉTODO AUXILIAR: Crear registro en tabla precio
   private void crearRegistroPrecio(Producto producto, Double valor) {
       try {
           Precio precio = new Precio();
           precio.setProducto(producto);
           precio.setValor(valor);
           precio.setFecha(java.time.LocalDateTime.now());
           
           Precio precioGuardado = precioRepository.save(precio);
           System.out.println("💾 Precio guardado: ID=" + precioGuardado.getId() + ", Valor=$" + valor);
           
       } catch (Exception e) {
           System.err.println("❌ Error creando precio: " + e.getMessage());
           e.printStackTrace();
       }
   }

   @PostMapping("/productos/{id}/eliminar")
   public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
       try {
           Producto producto = productoService.obtenerPorId(id);
           String nombreProducto = producto.getNombre();
           
           productoService.eliminar(id);
           
           redirectAttributes.addFlashAttribute("success", 
               "Producto '" + nombreProducto + "' eliminado exitosamente");
               
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("error", 
               "Error al eliminar producto: " + e.getMessage());
           e.printStackTrace();
       }
       return "redirect:/admin/productos";
   }

   // ======================
   // ENDPOINTS DE DEBUG BÁSICOS (sin métodos faltantes)
   // ======================

   @GetMapping("/debug-productos")
   @ResponseBody
   public String debugProductos() {
       try {
           List<Producto> productos = productoService.obtenerTodos();
           StringBuilder result = new StringBuilder();
           result.append("<h2>🔍 DEBUG - Productos básico</h2>");
           result.append("<p><a href='/admin/productos'>← Volver a Gestión de Productos</a></p>");
           result.append("<p><strong>Total productos:</strong> ").append(productos.size()).append("</p>");
           
           for (Producto p : productos) {
               result.append("<div style='border:2px solid #ccc; margin:15px 0; padding:15px; border-radius:8px;'>")
                     .append("<h3>").append(p.getNombre()).append("</h3>")
                     .append("<strong>ID:</strong> ").append(p.getId()).append("<br>")
                     .append("<strong>Código:</strong> ").append(p.getCodigo()).append("<br>")
                     .append("<strong>Marca:</strong> ").append(p.getMarca() != null ? p.getMarca() : "NULL").append("<br>")
                     .append("<strong>Precio actual:</strong> $").append(p.getPrecio()).append("<br>")
                     .append("</div>");
           }
           
           return result.toString();
       } catch (Exception e) {
           return "<h2>❌ Error en debug:</h2><p>" + e.getMessage() + "</p>";
       }
   }

   @GetMapping("/test-crear-producto")
   @ResponseBody
   public String testCrearProducto() {
       try {
           Producto testProducto = new Producto();
           testProducto.setCodigo("TEST" + System.currentTimeMillis());
           testProducto.setNombre("Producto Test con Precio");
           testProducto.setMarca("MarcaTest");
           testProducto.setCategoria("Herramientas");
           testProducto.setStock(99);
           testProducto.setPrecio(15990.0);
           testProducto.setDescripcion("Producto para testing de precios");
           
           Producto productoGuardado = productoService.crear(testProducto);
           
           // ✅ Asegurar que se cree el precio
           crearRegistroPrecio(productoGuardado, testProducto.getPrecio());
           
           return "<h2>✅ Producto de prueba creado con precio!</h2>" +
                  "<p><strong>ID:</strong> " + productoGuardado.getId() + "</p>" +
                  "<p><strong>Código:</strong> " + productoGuardado.getCodigo() + "</p>" +
                  "<p><strong>Precio:</strong> $" + productoGuardado.getPrecio() + "</p>" +
                  "<p><a href='/admin/productos'>Ver en lista de productos</a></p>";
                  
       } catch (Exception e) {
           return "<h2>❌ Error:</h2><p>" + e.getMessage() + "</p>";
       }
   }

   // API: Métricas para dashboard admin
   @GetMapping("/api/dashboard/metricas")
   @ResponseBody
   public ResponseEntity<?> metricasDashboard() {
       Map<String, Object> data = new HashMap<>();
       data.put("ventasDia", 0); // TODO: implementar en PagoService
       data.put("ventasSemana", 0); // TODO: implementar en PagoService
       data.put("ventasMes", 0); // TODO: implementar en PagoService
       data.put("usuariosHoy", usuarioService.contarUsuariosHoy());
       data.put("usuariosSemana", usuarioService.contarUsuariosSemana());
       data.put("usuariosTotal", usuarioService.contarUsuariosTotal());
       data.put("mensajesPendientes", mensajeService.contarPorEstado(Mensaje.EstadoMensaje.PENDIENTE));
       data.put("stockBajo", productoService.buscarPorStockMenorA(10).size());
       return ResponseEntity.ok(data);
   }

   // API: Mensajes por estado
   @GetMapping("/api/dashboard/mensajes-por-estado")
   @ResponseBody
   public ResponseEntity<?> mensajesPorEstado() {
       Map<String, Long> data = new HashMap<>();
       for (Mensaje.EstadoMensaje estado : Mensaje.EstadoMensaje.values()) {
           data.put(estado.name(), mensajeService.contarPorEstado(estado));
       }
       return ResponseEntity.ok(data);
   }

   // API: Productos por categoría
   @GetMapping("/api/dashboard/productos-por-categoria")
   @ResponseBody
   public ResponseEntity<?> productosPorCategoria() {
       List<cl.ferremas.model.Producto> productos = productoService.obtenerTodos();
       Map<String, Long> data = productos.stream().collect(Collectors.groupingBy(cl.ferremas.model.Producto::getCategoria, Collectors.counting()));
       return ResponseEntity.ok(data);
   }

   // API: Stock por sucursal
   @GetMapping("/api/dashboard/stock-por-sucursal")
   @ResponseBody
   public ResponseEntity<?> stockPorSucursal() {
       List<cl.ferremas.model.StockSucursal> stocks = stockSucursalService.obtenerTodos();
       Map<String, Integer> data = new HashMap<>();
       for (cl.ferremas.model.StockSucursal ss : stocks) {
           String nombre = ss.getSucursal() != null ? ss.getSucursal().getNombre() : "Sin Sucursal";
           data.put(nombre, data.getOrDefault(nombre, 0) + (ss.getStock() != null ? ss.getStock() : 0));
       }
       return ResponseEntity.ok(data);
   }

   // API: Productos con stock crítico
   @GetMapping("/api/dashboard/stock-critico")
   @ResponseBody
   public ResponseEntity<?> productosStockCritico() {
       List<cl.ferremas.model.Producto> criticos = productoService.buscarPorStockMenorA(5);
       return ResponseEntity.ok(criticos);
   }

   // API: Mensajes pendientes
   @GetMapping("/api/dashboard/mensajes-pendientes")
   @ResponseBody
   public ResponseEntity<?> mensajesPendientes() {
       return ResponseEntity.ok(mensajeService.obtenerPendientes());
   }

   // API: Órdenes pendientes (placeholder)
   @GetMapping("/api/dashboard/ordenes-pendientes")
   @ResponseBody
   public ResponseEntity<?> ordenesPendientes() {
       return ResponseEntity.ok(List.of()); // TODO: implementar si existe entidad Orden
   }

   // API: Productos más vistos (placeholder)
   @GetMapping("/api/dashboard/productos-mas-vistos")
   @ResponseBody
   public ResponseEntity<?> productosMasVistos() {
       return ResponseEntity.ok(List.of()); // TODO: implementar tracking de vistas
   }
}