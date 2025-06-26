package cl.ferremas.controller;

import cl.ferremas.model.Rol;
import cl.ferremas.model.Usuario;
import cl.ferremas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class WebController {

    @Autowired
    private UsuarioService usuarioService;

    // Página principal - redirige al login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    // Mostrar página de login
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        
        if (success != null) {
            model.addAttribute("success", "Cuenta creada exitosamente. Ya puedes iniciar sesión.");
        }
        
        return "login";
    }

    // Mostrar página de registro
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // Procesar registro de nuevo usuario
    @PostMapping("/register")
    public String processRegister(@RequestParam String nombre,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/register";
            }

            // Validar longitud mínima
            if (password.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/register";
            }

            // Verificar si el email ya existe
            if (usuarioService.existeEmail(email)) {
                redirectAttributes.addFlashAttribute("error", "Ya existe una cuenta con este email");
                return "redirect:/register";
            }

            // Crear nuevo usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setUsername(email);
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setRol(Rol.USER);

            usuarioService.crearUsuario(nuevoUsuario);
            
            // Redirigir al login con mensaje de éxito
            return "redirect:/login?success=true";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cuenta: " + e.getMessage());
            return "redirect:/register";
        }
    }

    // Dashboard después del login
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            Usuario usuario = usuarioService.buscarPorEmail(username);
            model.addAttribute("usuario", usuario);
            return "dashboard";
        } catch (Exception e) {
            return "redirect:/login?error=true";
        }
    }

    // Página de acceso denegado
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "redirect:/dashboard"; // Por ahora redirige al dashboard
    }
}