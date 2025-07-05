package cl.ferremas.controller.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import cl.ferremas.service.ProductoCatalogoService;
import cl.ferremas.model.Producto;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Optional;
import cl.ferremas.service.FavoritoService;
import cl.ferremas.model.ProductoFavorito;
import cl.ferremas.service.AuthService;
import cl.ferremas.service.SucursalService;
import cl.ferremas.model.Sucursal;
import cl.ferremas.service.MensajeService;
import cl.ferremas.model.Mensaje;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import cl.ferremas.model.Usuario;
import cl.ferremas.model.Rol;

@Controller
public class WebController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductoCatalogoService productoCatalogoService;

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private MensajeService mensajeService;

    // Página principal - redirige al login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Mostrar página de login






    // Dashboard después del login
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Verificar si el usuario está autenticado
        if (!authService.isUsuarioAutenticado()) {
            return "redirect:/login?redirect=/dashboard";
        }
        
        // Obtener usuario autenticado
        Usuario usuario = authService.getUsuarioActual();
        model.addAttribute("usuario", usuario);
        
        return "dashboard";
    }

    // Endpoint de prueba simple
    @GetMapping("/test-dashboard")
    public String testDashboard(Model model) {
        System.out.println("🔍 DEBUG: Accediendo a test-dashboard");
        model.addAttribute("test", "Dashboard de prueba funcionando");
        return "dashboard";
    }

    // Endpoint de prueba muy simple
    @GetMapping("/test-simple")
    public String testSimple() {
        System.out.println("🔍 DEBUG: Accediendo a test-simple");
        return "test-simple";
    }



    // =====================
    // CATÁLOGO PÚBLICO
    // =====================

    // Página principal del catálogo
    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "precioMin", required = false) Double precioMin,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
            @RequestParam(value = "sucursal", required = false) Long sucursalId,
            @RequestParam(value = "stock", required = false) Boolean soloStock,
            @RequestParam(value = "q", required = false) String busqueda,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        // Filtros combinables
        List<Producto> productos = productoCatalogoService.obtenerCatalogo(
                Optional.ofNullable(categoria),
                Optional.ofNullable(precioMin),
                Optional.ofNullable(precioMax),
                Optional.ofNullable(marca),
                Optional.ofNullable(sucursalId),
                Optional.ofNullable(soloStock),
                Optional.ofNullable(busqueda)
        );
        
        // Obtener información de favoritos para el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            try {
                Usuario usuario = (Usuario) auth.getPrincipal();
                List<ProductoFavorito> favoritos = favoritoService.listarFavoritos(usuario.getId());
                java.util.Set<Long> favoritosIds = favoritos.stream()
                    .map(f -> f.getProducto().getId())
                    .collect(java.util.stream.Collectors.toSet());
                
                // Marcar productos como favoritos
                productos.forEach(producto -> {
                    producto.setFavorito(favoritosIds.contains(producto.getId()));
                });
            } catch (Exception e) {
                // Si hay error, continuar sin favoritos
            }
        }
        
        // Paginación simple (12 productos por página)
        int pageSize = 12;
        int total = productos.size();
        int from = Math.max(0, (page - 1) * pageSize);
        int to = Math.min(from + pageSize, total);
        List<Producto> pageProductos = productos.subList(from, to);

        model.addAttribute("productos", pageProductos);
        model.addAttribute("categorias", productoCatalogoService.obtenerCategorias());
        model.addAttribute("marcas", productoCatalogoService.obtenerMarcas());
        model.addAttribute("sucursales", productoCatalogoService.obtenerSucursales());
        model.addAttribute("total", total);
        model.addAttribute("page", page);
        model.addAttribute("pages", (int) Math.ceil((double) total / pageSize));
        model.addAttribute("filtros", new java.util.HashMap<String, Object>() {{
            put("categoria", categoria);
            put("marca", marca);
            put("precioMin", precioMin);
            put("precioMax", precioMax);
            put("sucursal", sucursalId);
            put("stock", soloStock);
            put("busqueda", busqueda);
        }});
        model.addAttribute("valorDolar", productoCatalogoService.obtenerValorDolar());
        
        // Manejar mensaje de logout exitoso
        if ("success".equals(logout)) {
            model.addAttribute("logoutMessage", "Sesión cerrada exitosamente");
        }
        
        return "catalogo";
    }

    // Detalle de producto
    @GetMapping("/catalogo/producto/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        try {
            System.out.println("🔍 DEBUG: WebController.detalleProducto - Buscando producto ID: " + id);
            
            Producto producto = productoCatalogoService.obtenerDetalle(id);
            if (producto == null) {
                System.out.println("🔍 DEBUG: Producto no encontrado con ID: " + id);
                model.addAttribute("error", "Producto no encontrado");
                return "error";
            }
            
            System.out.println("🔍 DEBUG: Producto encontrado: " + producto.getNombre());
            
            // Verificar si el producto es favorito del usuario autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                try {
                    Usuario usuario = (Usuario) auth.getPrincipal();
                    boolean esFavorito = favoritoService.esFavorito(usuario.getId(), id);
                    producto.setFavorito(esFavorito);
                    System.out.println("🔍 DEBUG: Estado favorito: " + esFavorito);
                } catch (Exception e) {
                    System.out.println("🔍 DEBUG: Error al verificar favorito: " + e.getMessage());
                    // Si hay error, continuar sin favoritos
                }
            }
            
            // Obtener valor del dólar
            Double valorDolar = productoCatalogoService.obtenerValorDolar();
            System.out.println("🔍 DEBUG: Valor dólar: " + valorDolar);
            
            model.addAttribute("producto", producto);
            model.addAttribute("valorDolar", valorDolar);
            
            // Productos relacionados (misma categoría, excluyendo el actual)
            if (producto.getCategoria() != null) {
                List<Producto> relacionados = productoCatalogoService.obtenerCatalogo(
                    Optional.of(producto.getCategoria()),
                    Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.empty()
                ).stream().filter(p -> !p.getId().equals(producto.getId())).toList();
                model.addAttribute("productosRelacionados", relacionados);
                System.out.println("🔍 DEBUG: Productos relacionados encontrados: " + relacionados.size());
            } else {
                model.addAttribute("productosRelacionados", List.of());
                System.out.println("🔍 DEBUG: No hay productos relacionados (categoría null)");
            }
            
            System.out.println("🔍 DEBUG: Model configurado correctamente, retornando vista");
            return "producto-detalle";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en detalleProducto: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el detalle del producto: " + e.getMessage());
            return "error";
        }
    }

    // Productos por categoría
    @GetMapping("/catalogo/categoria/{categoria}")
    public String productosPorCategoria(@PathVariable String categoria, Model model) {
        List<Producto> productos = productoCatalogoService.obtenerCatalogo(
                Optional.ofNullable(categoria),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(false), Optional.empty()
        );
        model.addAttribute("productos", productos);
        model.addAttribute("categoria", categoria);
        model.addAttribute("valorDolar", productoCatalogoService.obtenerValorDolar());
        return "catalogo";
    }

    // Endpoint AJAX para búsqueda y filtros dinámicos
    @GetMapping("/api/catalogo/buscar")
    @ResponseBody
    public ResponseEntity<List<Producto>> buscarCatalogo(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "marca", required = false) String marca,
            @RequestParam(value = "precioMin", required = false) Double precioMin,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
            @RequestParam(value = "sucursal", required = false) Long sucursalId,
            @RequestParam(value = "stock", required = false) Boolean soloStock,
            @RequestParam(value = "q", required = false) String busqueda
    ) {
        List<Producto> productos = productoCatalogoService.obtenerCatalogo(
                Optional.ofNullable(categoria),
                Optional.ofNullable(precioMin),
                Optional.ofNullable(precioMax),
                Optional.ofNullable(marca),
                Optional.ofNullable(sucursalId),
                Optional.ofNullable(soloStock),
                Optional.ofNullable(busqueda)
        );
        return ResponseEntity.ok(productos);
    }

    // Vista del carrito
    @GetMapping("/carrito")
    public String carrito(Model model) {
        // El carrito ahora se maneja completamente desde el frontend con localStorage
        // Esta vista solo renderiza el template, el contenido se carga via AJAX
        return "carrito";
    }

    // Vista de checkout
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout";
    }

    // Vista de sucursales
    @GetMapping("/sucursales")
    public String sucursales(Model model) {
        try {
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            System.out.println("🔍 DEBUG: Sucursales encontradas: " + sucursales.size());
            
            model.addAttribute("sucursales", sucursales);
            
            // Obtener ciudades únicas para filtros
            List<String> ciudades = sucursales.stream()
                .map(Sucursal::getCiudad)
                .distinct()
                .sorted()
                .collect(java.util.stream.Collectors.toList());
            model.addAttribute("ciudades", ciudades);
            
            return "sucursales";
        } catch (Exception e) {
            System.err.println("❌ ERROR en sucursales: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las sucursales: " + e.getMessage());
            return "error";
        }
    }

    // Vista de contacto
    @GetMapping("/contacto")
    public String contacto(Model model) {
        try {
            // Obtener información de contacto de las sucursales
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            model.addAttribute("sucursales", sucursales);
            
            // Obtener usuario autenticado si existe
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                try {
                    Usuario usuario = (Usuario) auth.getPrincipal();
                    model.addAttribute("usuario", usuario);
                } catch (Exception e) {
                    // Si hay error, continuar sin usuario
                }
            }
            
            return "contacto";
        } catch (Exception e) {
            System.err.println("❌ ERROR en contacto: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar la página de contacto: " + e.getMessage());
            return "error";
        }
    }

    // Procesar formulario de contacto
    @PostMapping("/contacto")
    public String procesarContacto(
            @RequestParam String nombreCliente,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String asunto,
            @RequestParam String mensaje,
            Model model
    ) {
        try {
            System.out.println("📧 DEBUG: Procesando mensaje de contacto de: " + nombreCliente);
            
            // Crear nuevo mensaje
            Mensaje nuevoMensaje = new Mensaje();
            nuevoMensaje.setNombreCliente(nombreCliente);
            nuevoMensaje.setEmail(email);
            nuevoMensaje.setTelefono(telefono);
            nuevoMensaje.setAsunto(asunto);
            nuevoMensaje.setMensaje(mensaje);
            nuevoMensaje.setFechaCreacion(java.time.LocalDateTime.now());
            nuevoMensaje.setEstado(Mensaje.EstadoMensaje.PENDIENTE);
            
            // Asignar usuario si está autenticado
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
                try {
                    Usuario usuario = (Usuario) auth.getPrincipal();
                    nuevoMensaje.setUsuario(usuario);
                } catch (Exception e) {
                    // Si hay error, continuar sin usuario
                }
            }
            
            // Guardar mensaje
            mensajeService.guardar(nuevoMensaje);
            
            System.out.println("✅ Mensaje guardado con ID: " + nuevoMensaje.getId());
            
            model.addAttribute("success", "¡Mensaje enviado exitosamente! Nos pondremos en contacto contigo pronto.");
            
            // Recargar datos para la vista
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            model.addAttribute("sucursales", sucursales);
            
            return "contacto";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR al procesar contacto: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al enviar el mensaje: " + e.getMessage());
            
            // Recargar datos para la vista
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            model.addAttribute("sucursales", sucursales);
            
            return "contacto";
        }
    }

    // Página de favoritos del usuario
    @GetMapping("/mis-favoritos")
    public String misFavoritos(@AuthenticationPrincipal cl.ferremas.model.Usuario usuario, Model model) {
        System.out.println("🔍 DEBUG: misFavoritos - Usuario: " + (usuario != null ? usuario.getEmail() : "null"));
        
        if (usuario == null) {
            System.out.println("🔍 DEBUG: Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }
        
        try {
            var favoritos = favoritoService.listarFavoritos(usuario.getId());
            System.out.println("🔍 DEBUG: Favoritos encontrados: " + favoritos.size());
            
            model.addAttribute("favoritos", favoritos);
            model.addAttribute("valorDolar", productoCatalogoService.obtenerValorDolar());
            
            System.out.println("🔍 DEBUG: Model configurado, retornando vista mis-favoritos");
            return "mis-favoritos";
        } catch (Exception e) {
            System.err.println("❌ ERROR en misFavoritos: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("mensaje", "Error al cargar favoritos: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint de debug para verificar productos
    @GetMapping("/debug/productos")
    @ResponseBody
    public String debugProductos() {
        try {
            List<Producto> productos = productoCatalogoService.obtenerCatalogo(
                Optional.empty(), Optional.empty(), Optional.empty(), 
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
            );
            
            StringBuilder result = new StringBuilder();
            result.append("<h2>🔍 DEBUG - Productos en Base de Datos</h2>");
            result.append("<p><strong>Total productos:</strong> ").append(productos.size()).append("</p>");
            result.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
            result.append("<tr><th>ID</th><th>Código</th><th>Nombre</th><th>Categoría</th><th>Precio</th><th>Stock</th></tr>");
            
            for (Producto p : productos) {
                result.append("<tr>")
                      .append("<td>").append(p.getId()).append("</td>")
                      .append("<td>").append(p.getCodigo()).append("</td>")
                      .append("<td>").append(p.getNombre()).append("</td>")
                      .append("<td>").append(p.getCategoria()).append("</td>")
                      .append("<td>").append(p.getPrecio()).append("</td>")
                      .append("<td>").append(p.getStock()).append("</td>")
                      .append("</tr>");
            }
            result.append("</table>");
            
            return result.toString();
        } catch (Exception e) {
            return "<h2>❌ Error en debug:</h2><p>" + e.getMessage() + "</p>";
        }
    }

    // Endpoint de debug para verificar sucursales
    @GetMapping("/debug/sucursales")
    @ResponseBody
    public String debugSucursales() {
        try {
            List<Sucursal> sucursales = sucursalService.obtenerTodas();
            StringBuilder sb = new StringBuilder();
            sb.append("=== SUCURSALES EN BD ===\n");
            for (Sucursal s : sucursales) {
                sb.append(String.format("ID: %d | Nombre: %s | Ciudad: %s | Tel: %s\n", 
                    s.getId(), s.getNombre(), s.getCiudad(), s.getTelefono()));
            }
            return sb.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    // =====================
    // PANEL DE ADMINISTRACIÓN DE MENSAJES
    // =====================

    // Vista del panel de mensajes (solo para admins)
    @GetMapping("/admin/mensajes")
    public String adminMensajes(Model model) {
        try {
            // Verificar si el usuario está autenticado y es admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return "redirect:/login?redirect=/admin/mensajes";
            }
            
            Usuario usuario = (Usuario) auth.getPrincipal();
            if (!usuario.getRol().equals(Rol.ADMIN)) {
                model.addAttribute("error", "Acceso denegado. Solo administradores pueden acceder a esta página.");
                return "error";
            }
            
            // Obtener mensajes por estado
            List<Mensaje> mensajesPendientes = mensajeService.obtenerPorEstado(Mensaje.EstadoMensaje.PENDIENTE);
            List<Mensaje> mensajesEnProceso = mensajeService.obtenerPorEstado(Mensaje.EstadoMensaje.EN_PROCESO);
            List<Mensaje> mensajesResueltos = mensajeService.obtenerPorEstado(Mensaje.EstadoMensaje.RESUELTO);
            
            // Contadores
            long totalPendientes = mensajeService.contarPorEstado(Mensaje.EstadoMensaje.PENDIENTE);
            long totalEnProceso = mensajeService.contarPorEstado(Mensaje.EstadoMensaje.EN_PROCESO);
            long totalResueltos = mensajeService.contarPorEstado(Mensaje.EstadoMensaje.RESUELTO);
            
            model.addAttribute("mensajesPendientes", mensajesPendientes);
            model.addAttribute("mensajesEnProceso", mensajesEnProceso);
            model.addAttribute("mensajesResueltos", mensajesResueltos);
            model.addAttribute("totalPendientes", totalPendientes);
            model.addAttribute("totalEnProceso", totalEnProceso);
            model.addAttribute("totalResueltos", totalResueltos);
            model.addAttribute("usuario", usuario);
            
            return "admin/mensajes";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR en adminMensajes: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar el panel de mensajes: " + e.getMessage());
            return "error";
        }
    }

    // Responder a un mensaje
    @PostMapping("/admin/mensajes/{id}/responder")
    public String responderMensaje(
            @PathVariable Long id,
            @RequestParam String respuesta,
            @RequestParam Mensaje.EstadoMensaje nuevoEstado,
            Model model
    ) {
        try {
            // Verificar permisos de admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return "redirect:/login";
            }
            
            Usuario usuario = (Usuario) auth.getPrincipal();
            if (!usuario.getRol().equals(Rol.ADMIN)) {
                model.addAttribute("error", "Acceso denegado.");
                return "error";
            }
            
            // Buscar el mensaje
            Mensaje mensaje = mensajeService.obtenerPorId(id);
            if (mensaje == null) {
                model.addAttribute("error", "Mensaje no encontrado");
                return "error";
            }
            
            // Actualizar mensaje
            mensaje.setRespuestaVendedor(respuesta);
            mensaje.setFechaRespuesta(java.time.LocalDateTime.now());
            mensaje.setEstado(nuevoEstado);
            mensaje.setAdminUsuario(usuario);
            
            mensajeService.guardar(mensaje);
            
            System.out.println("✅ Mensaje respondido: " + id);
            
            // Redirigir de vuelta al panel
            return "redirect:/admin/mensajes?success=Respuesta enviada exitosamente";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR al responder mensaje: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al responder el mensaje: " + e.getMessage());
            return "error";
        }
    }

    // Cambiar estado de un mensaje
    @PostMapping("/admin/mensajes/{id}/estado")
    public String cambiarEstadoMensaje(
            @PathVariable Long id,
            @RequestParam Mensaje.EstadoMensaje nuevoEstado,
            Model model
    ) {
        try {
            // Verificar permisos de admin
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
                return "redirect:/login";
            }
            
            Usuario usuario = (Usuario) auth.getPrincipal();
            if (!usuario.getRol().equals(Rol.ADMIN)) {
                model.addAttribute("error", "Acceso denegado.");
                return "error";
            }
            
            // Buscar y actualizar el mensaje
            Mensaje mensaje = mensajeService.obtenerPorId(id);
            if (mensaje == null) {
                model.addAttribute("error", "Mensaje no encontrado");
                return "error";
            }
            
            mensaje.setEstado(nuevoEstado);
            mensajeService.guardar(mensaje);
            
            System.out.println("✅ Estado de mensaje cambiado: " + id + " -> " + nuevoEstado);
            
            return "redirect:/admin/mensajes?success=Estado actualizado exitosamente";
            
        } catch (Exception e) {
            System.err.println("❌ ERROR al cambiar estado: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cambiar el estado: " + e.getMessage());
            return "error";
        }
    }
}