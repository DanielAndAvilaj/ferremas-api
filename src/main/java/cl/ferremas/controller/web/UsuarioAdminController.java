package cl.ferremas.controller.web;

import cl.ferremas.model.Usuario;
import cl.ferremas.model.Rol;
import cl.ferremas.service.UsuarioService;
import cl.ferremas.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioAdminController {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private MensajeService mensajeService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioAdminController.class);

    @GetMapping
    public String listarUsuarios(@RequestParam(required = false) String busqueda,
                                 @RequestParam(required = false) String rol,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model, RedirectAttributes redirectAttributes) {
        try {
            logger.info("[USUARIOS] GET /admin/usuarios - Iniciando con parámetros: busqueda='{}', rol='{}', page={}, size={}", busqueda, rol, page, size);
            
            Pageable pageable = PageRequest.of(page, size);
            Rol rolEnum = null;
            
            if (rol != null && !rol.isBlank()) {
                try {
                    rolEnum = Rol.valueOf(rol.trim().toUpperCase());
                    logger.info("[USUARIOS] Rol convertido exitosamente: {}", rolEnum);
                } catch (IllegalArgumentException e) {
                    logger.warn("[USUARIOS] Rol inválido recibido: '{}'. Se ignora el filtro de rol.", rol);
                    rolEnum = null;
                }
            }
            
            logger.info("[USUARIOS] Llamando a usuarioService.buscarUsuarios...");
            Page<Usuario> usuariosPage = usuarioService.buscarUsuarios(busqueda, rolEnum, pageable);
            logger.info("[USUARIOS] Usuarios obtenidos: {} de {} total", usuariosPage.getContent().size(), usuariosPage.getTotalElements());
            
            model.addAttribute("usuariosPage", usuariosPage);
            model.addAttribute("busqueda", busqueda);
            model.addAttribute("rol", rolEnum);
            model.addAttribute("totalUsuarios", usuarioService.contarUsuariosTotal());
            model.addAttribute("usuariosActivos", usuariosPage.getContent().stream().filter(Usuario::isEnabled).count());
            model.addAttribute("usuariosAdmin", usuariosPage.getContent().stream().filter(u -> u.getRol() == Rol.ADMIN).count());
            
            logger.info("[USUARIOS] Model configurado exitosamente, retornando vista");
            return "admin/usuarios";
            
        } catch (Exception ex) {
            logger.error("[USUARIOS] Error crítico al listar usuarios", ex);
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al cargar los usuarios: " + ex.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @GetMapping("/{id}")
    public String detalleUsuario(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        // TODO: agregar compras y mensajes del usuario
        model.addAttribute("mensajes", mensajeService.obtenerPorUsuario(id));
        return "admin/usuario-detalle";
    }

    @PostMapping("/{id}/rol")
    public String cambiarRol(@PathVariable Long id, @RequestParam Rol rol) {
        usuarioService.cambiarRol(id, rol);
        return "redirect:/admin/usuarios/" + id;
    }

    @PostMapping("/{id}/activar")
    public String activarUsuario(@PathVariable Long id) {
        usuarioService.activarUsuario(id);
        return "redirect:/admin/usuarios/" + id;
    }

    @PostMapping("/{id}/desactivar")
    public String desactivarUsuario(@PathVariable Long id) {
        usuarioService.desactivarUsuario(id);
        return "redirect:/admin/usuarios/" + id;
    }

    // FORMULARIO DE EDICIÓN DE USUARIO
    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.obtenerPorId(id);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/admin/usuarios";
            }
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", Rol.values());
            return "admin/usuario-form";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // GUARDAR EDICIÓN DE USUARIO
    @PostMapping("/editar/{id}")
    public String guardarEdicionUsuario(@PathVariable Long id, @ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.actualizarUsuario(id, usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    // ELIMINAR USUARIO
    @PostMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.eliminarUsuario(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException ex, RedirectAttributes redirectAttributes) {
        if (ex.getName().equals("rol")) {
            redirectAttributes.addFlashAttribute("error", "El filtro de rol es inválido. Por favor selecciona un valor válido.");
            return "redirect:/admin/usuarios";
        }
        redirectAttributes.addFlashAttribute("error", "Error de parámetros: " + ex.getMessage());
        return "redirect:/admin/usuarios";
    }
    
    // Método de prueba para diagnosticar problemas
    @GetMapping("/debug")
    @ResponseBody
    public String debugUsuarios() {
        try {
            logger.info("[USUARIOS DEBUG] Iniciando diagnóstico...");
            
            // Verificar conexión a base de datos
            long totalUsuarios = usuarioService.contarUsuariosTotal();
            logger.info("[USUARIOS DEBUG] Total de usuarios en BD: {}", totalUsuarios);
            
            // Obtener algunos usuarios
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            logger.info("[USUARIOS DEBUG] Usuarios obtenidos: {}", usuarios.size());
            
            StringBuilder result = new StringBuilder();
            result.append("=== DIAGNÓSTICO USUARIOS ===\n");
            result.append("Total usuarios: ").append(totalUsuarios).append("\n");
            result.append("Usuarios obtenidos: ").append(usuarios.size()).append("\n\n");
            
            if (!usuarios.isEmpty()) {
                result.append("Primeros 5 usuarios:\n");
                usuarios.stream().limit(5).forEach(u -> {
                    result.append("- ID: ").append(u.getId())
                          .append(", Nombre: ").append(u.getNombre())
                          .append(", Email: ").append(u.getEmail())
                          .append(", Rol: ").append(u.getRol())
                          .append(", Activo: ").append(u.isEnabled())
                          .append("\n");
                });
            }
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("[USUARIOS DEBUG] Error en diagnóstico", e);
            return "ERROR: " + e.getMessage() + "\n" + e.getClass().getSimpleName();
        }
    }
    
    // Endpoint público para pruebas (sin autenticación)
    @GetMapping("/public-debug")
    @ResponseBody
    @PreAuthorize("permitAll()")
    public String publicDebugUsuarios() {
        try {
            logger.info("[USUARIOS PUBLIC DEBUG] Iniciando diagnóstico público...");
            
            // Verificar conexión a base de datos
            long totalUsuarios = usuarioService.contarUsuariosTotal();
            logger.info("[USUARIOS PUBLIC DEBUG] Total de usuarios en BD: {}", totalUsuarios);
            
            StringBuilder result = new StringBuilder();
            result.append("=== DIAGNÓSTICO PÚBLICO USUARIOS ===\n");
            result.append("Total usuarios: ").append(totalUsuarios).append("\n");
            result.append("Estado: OK\n");
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("[USUARIOS PUBLIC DEBUG] Error en diagnóstico público", e);
            return "ERROR: " + e.getMessage() + "\n" + e.getClass().getSimpleName();
        }
    }
} 