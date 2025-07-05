package cl.ferremas.controller.api;

import cl.ferremas.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @Autowired
    private AuthService authService;

    /**
     * API endpoint para obtener información del usuario actual
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> obtenerUsuarioActual() {
        Map<String, Object> resultado = new HashMap<>();
        
        if (authService.isUsuarioAutenticado()) {
            resultado.put("authenticated", true);
            resultado.put("user", authService.getUsuarioActual());
        } else {
            resultado.put("authenticated", false);
            resultado.put("user", null);
        }
        
        return ResponseEntity.ok(resultado);
    }
} 