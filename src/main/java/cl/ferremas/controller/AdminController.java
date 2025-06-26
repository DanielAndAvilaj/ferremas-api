package cl.ferremas.controller;

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

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

   @Autowired
   private ProductoService productoService;

   @Autowired
   private PrecioRepository precioRepository;

   // Dashboard principal del admin
   @GetMapping
   public String adminDashboard(Model model) {
       try {
           List<Producto> productos = productoService.obtenerTodos();
           model.addAttribute("totalProductos", productos.size());
           
           long productosStockBajo = productos.stream()
               .filter(p -> p.getStock() != null && p.getStock() < 10)
               .count();
           model.addAttribute("productosStockBajo", productosStockBajo);
           
           model.addAttribute("productos", productos);
           
           return "admin/dashboard";
       } catch (Exception e) {
           model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
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
           
           model.addAttribute("productos", productos);
           System.out.println("🔍 DEBUG: Model configurado, retornando vista");
           
           return "admin/productos";
           
       } catch (Exception e) {
           System.err.println("❌ ERROR en listarProductos: " + e.getMessage());
           e.printStackTrace();
           
           model.addAttribute("error", "Error al cargar productos: " + e.getMessage());
           model.addAttribute("productos", List.of());
           
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
           productoActual.setPrecio(producto.getPrecio()); // ✅ Actualizar precio también
           
           // Guardar producto actualizado
           Producto productoActualizado = productoService.actualizar(id, productoActual);
           
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
           precio.setFecha(LocalDate.now()); // ✅ CORREGIDO: LocalDate.now()
           
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
}