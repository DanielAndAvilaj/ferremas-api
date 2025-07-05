package cl.ferremas.controller.web;

import cl.ferremas.model.Usuario;
import cl.ferremas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Controller
@RequestMapping("/debug")
public class DebugController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);
    
    @GetMapping("/usuarios")
    @ResponseBody
    public String testUsuarios() {
        try {
            logger.info("[DEBUG] Iniciando prueba de usuarios...");
            
            // Verificar conexión a base de datos
            long totalUsuarios = usuarioService.contarUsuariosTotal();
            logger.info("[DEBUG] Total de usuarios en BD: {}", totalUsuarios);
            
            // Obtener algunos usuarios
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            logger.info("[DEBUG] Usuarios obtenidos: {}", usuarios.size());
            
            StringBuilder result = new StringBuilder();
            result.append("=== PRUEBA DE USUARIOS ===\n");
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
            } else {
                result.append("No hay usuarios en la base de datos.\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("[DEBUG] Error en prueba de usuarios", e);
            return "ERROR: " + e.getMessage() + "\n" + e.getClass().getSimpleName() + "\n" + e.getStackTrace()[0];
        }
    }
    
    @GetMapping("/db")
    @ResponseBody
    public String testDatabase() {
        try {
            logger.info("[DEBUG] Probando conexión a base de datos...");
            
            // Prueba simple de conexión
            long totalUsuarios = usuarioService.contarUsuariosTotal();
            
            return "=== PRUEBA DE BASE DE DATOS ===\n" +
                   "Estado: CONECTADO\n" +
                   "Total usuarios: " + totalUsuarios + "\n" +
                   "Hora: " + java.time.LocalDateTime.now() + "\n";
                   
        } catch (Exception e) {
            logger.error("[DEBUG] Error en prueba de base de datos", e);
            return "ERROR DE BASE DE DATOS: " + e.getMessage() + "\n" + e.getClass().getSimpleName();
        }
    }
} 