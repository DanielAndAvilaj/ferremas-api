package cl.ferremas.controller.api;

import cl.ferremas.model.Usuario;
import cl.ferremas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/update-passwords")
    public String updatePasswords() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            
            StringBuilder result = new StringBuilder();
            result.append("Actualizando contraseñas...\n");
            
            for (Usuario usuario : usuarios) {
                if (!usuario.getEmail().equals("test@test.com")) { // No actualizar el que ya funciona
                    usuarioService.actualizarContraseña(usuario.getId(), "123456");
                    result.append("✓ Actualizado: ").append(usuario.getEmail()).append("\n");
                }
            }
            
            result.append("\nTodas las contraseñas han sido actualizadas a '123456'");
            return result.toString();
            
        } catch (Exception e) {
            return "Error actualizando contraseñas: " + e.getMessage();
        }
    }

    @GetMapping("/list-users")
    public String listUsers() {
        try {
            List<Usuario> usuarios = usuarioService.obtenerTodos();
            StringBuilder result = new StringBuilder();
            result.append("Usuarios en la base de datos:\n\n");
            
            for (Usuario usuario : usuarios) {
                result.append("ID: ").append(usuario.getId())
                      .append(" | Email: ").append(usuario.getEmail())
                      .append(" | Nombre: ").append(usuario.getNombre())
                      .append(" | Rol: ").append(usuario.getRol())
                      .append(" | Enabled: ").append(usuario.isEnabled())
                      .append("\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "Error listando usuarios: " + e.getMessage();
        }
    }

    @GetMapping("/eliminar-duplicados")
    public String eliminarDuplicados() {
        List<Usuario> usuarios = usuarioService.obtenerTodos();
        Map<String, Usuario> emailMap = new HashMap<>();
        List<Long> idsAEliminar = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getEmail() == null) continue;
            String email = u.getEmail().toLowerCase();
            if (!emailMap.containsKey(email)) {
                emailMap.put(email, u);
            } else {
                // Si ya existe, marcar este para eliminar
                idsAEliminar.add(u.getId());
            }
        }
        for (Long id : idsAEliminar) {
            usuarioService.eliminarUsuario(id);
        }
        return "Eliminados " + idsAEliminar.size() + " usuarios duplicados.";
    }
} 