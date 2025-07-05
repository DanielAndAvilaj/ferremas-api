package cl.ferremas.controller.web;

import cl.ferremas.service.AuthService;
import cl.ferremas.service.UsuarioService;
import cl.ferremas.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Muestra la página de login
     */
    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "redirect", required = false) String redirect,
            Model model) {
        
        // Si el usuario ya está autenticado, redirigir según su rol
        if (authService.isUsuarioAutenticado()) {
            return authService.tieneRol(cl.ferremas.model.Rol.ADMIN) ? "redirect:/admin" : "redirect:/dashboard";
        }
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (success != null) {
            model.addAttribute("success", "Cuenta creada exitosamente. Ya puedes iniciar sesión.");
        }
        
        // Preservar la URL de redirección si existe
        if (redirect != null && !redirect.isEmpty()) {
            model.addAttribute("redirectUrl", redirect);
        }
        
        return "login";
    }

    /**
     * Muestra la página de registro
     */
    @GetMapping("/register")
    public String mostrarRegistro() {
        // Si el usuario ya está autenticado, redirigir
        if (authService.isUsuarioAutenticado()) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    /**
     * Procesa el registro de usuario (POST)
     */
    @PostMapping("/register")
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        Map<String, Object> resultado = authService.registrarUsuario(nombre, email, password, confirmPassword);
        
        if ((Boolean) resultado.get("success")) {
            redirectAttributes.addFlashAttribute("success", resultado.get("message"));
            return "redirect:/login?success=true";
        } else {
            // Si hay errores específicos de validación
            if (resultado.containsKey("errors")) {
                @SuppressWarnings("unchecked")
                Map<String, String> errores = (Map<String, String>) resultado.get("errors");
                errores.forEach(redirectAttributes::addFlashAttribute);
            } else {
                redirectAttributes.addFlashAttribute("error", resultado.get("message"));
            }
            return "redirect:/register";
        }
    }

    /**
     * API endpoint para registro (JSON)
     */
    @PostMapping("/api/auth/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registrarUsuarioAPI(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword) {
        
        Map<String, Object> resultado = authService.registrarUsuario(nombre, email, password, confirmPassword);
        
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * API endpoint para login (JSON)
     */
    @PostMapping("/api/auth/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> loginAPI(
            @RequestParam String username,
            @RequestParam String password) {
        
        Map<String, Object> resultado = authService.autenticarUsuario(username, password);
        
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * API endpoint para logout (JSON)
     */
    @PostMapping("/api/auth/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logoutAPI() {
        Map<String, Object> resultado = authService.cerrarSesion();
        
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * Procesa el logout
     */
    @PostMapping("/logout")
    public String procesarLogout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Map<String, Object> resultado = authService.cerrarSesion();
        
        // Invalidar la sesión HTTP
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        
        if ((Boolean) resultado.get("success")) {
            redirectAttributes.addFlashAttribute("success", "Sesión cerrada exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", resultado.get("message"));
        }
        
        return "redirect:/catalogo?logout=success";
    }

    /**
     * Página de acceso denegado
     */
    @GetMapping("/access-denied")
    public String accesoDenegado(Model model) {
        model.addAttribute("error", "No tienes permisos para acceder a esta página");
        return "error";
    }

    /**
     * Página de perfil del usuario
     */
    @GetMapping("/perfil")
    public String mostrarPerfil(Model model) {
        if (!authService.isUsuarioAutenticado()) {
            return "redirect:/login?redirect=/perfil";
        }
        
        model.addAttribute("usuario", authService.getUsuarioActual());
        return "perfil";
    }

    /**
     * API endpoint para actualizar perfil
     */
    @PostMapping("/api/auth/profile")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> actualizarPerfilAPI(
            @RequestParam String nombre,
            @RequestParam String email) {
        
        if (!authService.isUsuarioAutenticado()) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Usuario no autenticado"
            ));
        }
        
        Long usuarioId = authService.getUsuarioActual().getId();
        Map<String, Object> resultado = authService.actualizarPerfil(usuarioId, nombre, email);
        
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * API endpoint para cambiar contraseña
     */
    @PostMapping("/api/auth/change-password")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cambiarContraseñaAPI(
            @RequestParam String contraseñaActual,
            @RequestParam String nuevaContraseña,
            @RequestParam String confirmarContraseña) {
        
        if (!authService.isUsuarioAutenticado()) {
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Usuario no autenticado"
            ));
        }
        
        Long usuarioId = authService.getUsuarioActual().getId();
        Map<String, Object> resultado = authService.cambiarContraseña(usuarioId, contraseñaActual, nuevaContraseña, confirmarContraseña);
        
        if ((Boolean) resultado.get("success")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.badRequest().body(resultado);
        }
    }

    /**
     * Verificar si el email está disponible
     */
    @GetMapping("/api/auth/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarEmail(@RequestParam String email) {
        boolean disponible = !usuarioService.existeEmail(email);
        
        return ResponseEntity.ok(Map.of(
            "available", disponible,
            "message", disponible ? "Email disponible" : "Email ya está en uso"
        ));
    }

    /**
     * Procesa la actualización de datos del perfil (formulario clásico)
     */
    @PostMapping("/perfil")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String email,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (!authService.isUsuarioAutenticado()) {
            return "redirect:/login?redirect=/perfil";
        }
        
        try {
            Long usuarioId = authService.getUsuarioActual().getId();
            Map<String, Object> resultado = authService.actualizarPerfil(usuarioId, nombre, email);
            
            if ((Boolean) resultado.get("success")) {
                // Actualizar la sesión del usuario con los nuevos datos
                Usuario usuarioActualizado = (Usuario) resultado.get("usuario");
                if (usuarioActualizado != null) {
                    // Actualizar la autenticación en el contexto de seguridad
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth != null && auth.getPrincipal() instanceof Usuario) {
                        // Crear nueva autenticación con el usuario actualizado
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                            usuarioActualizado, 
                            auth.getCredentials(), 
                            auth.getAuthorities()
                        );
                        SecurityContextHolder.getContext().setAuthentication(newAuth);
                    }
                }
                
                redirectAttributes.addFlashAttribute("success", "Datos actualizados exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", resultado.get("message"));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar los datos: " + e.getMessage());
        }
        
        return "redirect:/perfil";
    }

    /**
     * Procesa el cambio de contraseña (formulario clásico)
     */
    @PostMapping("/perfil/cambiar-password")
    public String cambiarContraseña(
            @RequestParam String passwordActual,
            @RequestParam String nuevaPassword,
            @RequestParam String confirmarPassword,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (!authService.isUsuarioAutenticado()) {
            return "redirect:/login?redirect=/perfil";
        }
        
        try {
            Long usuarioId = authService.getUsuarioActual().getId();
            Map<String, Object> resultado = authService.cambiarContraseña(usuarioId, passwordActual, nuevaPassword, confirmarPassword);
            
            if ((Boolean) resultado.get("success")) {
                redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", resultado.get("message"));
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar la contraseña: " + e.getMessage());
        }
        
        return "redirect:/perfil";
    }
}