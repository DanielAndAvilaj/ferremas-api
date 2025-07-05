package cl.ferremas.service;

import cl.ferremas.model.Usuario;
import cl.ferremas.model.Rol;
import cl.ferremas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    /**
     * Registra un nuevo usuario con validaciones completas
     */
    public Map<String, Object> registrarUsuario(String nombre, String email, String password, String confirmPassword) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Validaciones
            Map<String, String> errores = validarDatosRegistro(nombre, email, password, confirmPassword);
            if (!errores.isEmpty()) {
                resultado.put("success", false);
                resultado.put("errors", errores);
                return resultado;
            }

            // Verificar si el email ya existe
            if (usuarioRepository.existsByEmail(email)) {
                resultado.put("success", false);
                resultado.put("message", "Ya existe una cuenta con este email");
                return resultado;
            }

            // Crear usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombre(nombre.trim());
            nuevoUsuario.setEmail(email.toLowerCase().trim());
            nuevoUsuario.setUsername(email.toLowerCase().trim());
            nuevoUsuario.setPassword(passwordEncoder.encode(password));
            nuevoUsuario.setRol(Rol.USER);
            nuevoUsuario.setEnabled(true);
            nuevoUsuario.setFechaRegistro(LocalDateTime.now());

            usuarioRepository.save(nuevoUsuario);

            resultado.put("success", true);
            resultado.put("message", "Usuario registrado exitosamente");
            resultado.put("usuario", nuevoUsuario);

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error interno del servidor: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Autentica un usuario con manejo de errores detallado
     */
    public Map<String, Object> autenticarUsuario(String username, String password) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            // Validaciones básicas
            if (username == null || username.trim().isEmpty()) {
                resultado.put("success", false);
                resultado.put("message", "El email es requerido");
                return resultado;
            }

            if (password == null || password.trim().isEmpty()) {
                resultado.put("success", false);
                resultado.put("message", "La contraseña es requerida");
                return resultado;
            }

            // Intentar autenticación
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username.trim(), password)
            );

            // Si llega aquí, la autenticación fue exitosa
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            Usuario usuario = (Usuario) authentication.getPrincipal();
            
            resultado.put("success", true);
            resultado.put("message", "Autenticación exitosa");
            resultado.put("usuario", usuario);
            resultado.put("redirectUrl", usuario.getRol() == Rol.ADMIN ? "/admin" : "/dashboard");

        } catch (BadCredentialsException e) {
            resultado.put("success", false);
            resultado.put("message", "Email o contraseña incorrectos");
        } catch (DisabledException e) {
            resultado.put("success", false);
            resultado.put("message", "Tu cuenta ha sido deshabilitada. Contacta al administrador.");
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error durante la autenticación: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public Map<String, Object> cerrarSesion() {
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            SecurityContextHolder.clearContext();
            resultado.put("success", true);
            resultado.put("message", "Sesión cerrada exitosamente");
        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error al cerrar sesión: " + e.getMessage());
        }
        
        return resultado;
    }

    /**
     * Obtiene el usuario autenticado actual
     */
    public Usuario getUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal()) &&
            authentication.getPrincipal() instanceof Usuario) {
            return (Usuario) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Verifica si el usuario actual está autenticado
     */
    public boolean isUsuarioAutenticado() {
        return getUsuarioActual() != null;
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     */
    public boolean tieneRol(Rol rol) {
        Usuario usuario = getUsuarioActual();
        return usuario != null && usuario.getRol() == rol;
    }

    /**
     * Valida los datos de registro
     */
    private Map<String, String> validarDatosRegistro(String nombre, String email, String password, String confirmPassword) {
        Map<String, String> errores = new HashMap<>();

        // Validar nombre
        if (nombre == null || nombre.trim().isEmpty()) {
            errores.put("nombre", "El nombre es requerido");
        } else if (nombre.trim().length() < 2) {
            errores.put("nombre", "El nombre debe tener al menos 2 caracteres");
        } else if (nombre.trim().length() > 50) {
            errores.put("nombre", "El nombre no puede exceder 50 caracteres");
        }

        // Validar email
        if (email == null || email.trim().isEmpty()) {
            errores.put("email", "El email es requerido");
        } else if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            errores.put("email", "El formato del email no es válido");
        }

        // Validar contraseña
        if (password == null || password.isEmpty()) {
            errores.put("password", "La contraseña es requerida");
        } else if (password.length() < 8) {
            errores.put("password", "La contraseña debe tener al menos 8 caracteres");
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errores.put("password", "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial");
        }

        // Validar confirmación de contraseña
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            errores.put("confirmPassword", "Debes confirmar la contraseña");
        } else if (!password.equals(confirmPassword)) {
            errores.put("confirmPassword", "Las contraseñas no coinciden");
        }

        return errores;
    }

    /**
     * Actualiza la información del perfil del usuario
     */
    public Map<String, Object> actualizarPerfil(Long usuarioId, String nombre, String email) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar nombre
            if (nombre != null && !nombre.trim().isEmpty()) {
                if (nombre.trim().length() < 2) {
                    resultado.put("success", false);
                    resultado.put("message", "El nombre debe tener al menos 2 caracteres");
                    return resultado;
                }
                usuario.setNombre(nombre.trim());
            }

            // Validar email
            if (email != null && !email.trim().isEmpty()) {
                if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
                    resultado.put("success", false);
                    resultado.put("message", "El formato del email no es válido");
                    return resultado;
                }
                
                // Verificar si el email ya existe en otro usuario
                if (!email.equalsIgnoreCase(usuario.getEmail()) && 
                    usuarioRepository.existsByEmail(email.trim())) {
                    resultado.put("success", false);
                    resultado.put("message", "Ya existe una cuenta con este email");
                    return resultado;
                }
                
                usuario.setEmail(email.toLowerCase().trim());
                usuario.setUsername(email.toLowerCase().trim());
            }

            usuarioRepository.save(usuario);

            resultado.put("success", true);
            resultado.put("message", "Perfil actualizado exitosamente");
            resultado.put("usuario", usuario);

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error al actualizar perfil: " + e.getMessage());
        }

        return resultado;
    }

    /**
     * Cambia la contraseña del usuario
     */
    public Map<String, Object> cambiarContraseña(Long usuarioId, String contraseñaActual, String nuevaContraseña, String confirmarContraseña) {
        Map<String, Object> resultado = new HashMap<>();

        try {
            Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar contraseña actual
            if (contraseñaActual == null || !passwordEncoder.matches(contraseñaActual, usuario.getPassword())) {
                resultado.put("success", false);
                resultado.put("message", "La contraseña actual es incorrecta");
                return resultado;
            }

            // Validar nueva contraseña
            if (nuevaContraseña == null || nuevaContraseña.isEmpty()) {
                resultado.put("success", false);
                resultado.put("message", "La nueva contraseña es requerida");
                return resultado;
            }

            if (nuevaContraseña.length() < 8) {
                resultado.put("success", false);
                resultado.put("message", "La nueva contraseña debe tener al menos 8 caracteres");
                return resultado;
            }

            if (!PASSWORD_PATTERN.matcher(nuevaContraseña).matches()) {
                resultado.put("success", false);
                resultado.put("message", "La nueva contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial");
                return resultado;
            }

            if (!nuevaContraseña.equals(confirmarContraseña)) {
                resultado.put("success", false);
                resultado.put("message", "Las contraseñas no coinciden");
                return resultado;
            }

            // Actualizar contraseña
            usuario.setPassword(passwordEncoder.encode(nuevaContraseña));
            usuarioRepository.save(usuario);

            resultado.put("success", true);
            resultado.put("message", "Contraseña cambiada exitosamente");

        } catch (Exception e) {
            resultado.put("success", false);
            resultado.put("message", "Error al cambiar contraseña: " + e.getMessage());
        }

        return resultado;
    }
} 