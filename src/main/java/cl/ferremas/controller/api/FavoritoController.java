package cl.ferremas.controller.api;

import cl.ferremas.model.ProductoFavorito;
import cl.ferremas.model.Usuario;
import cl.ferremas.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {
    @Autowired
    private FavoritoService favoritoService;

    @PostMapping("/{productoId}")
    public ResponseEntity<?> agregar(@PathVariable Long productoId, @AuthenticationPrincipal Usuario usuario) {
        try {
            ProductoFavorito fav = favoritoService.agregarFavorito(usuario.getId(), productoId);
            return ResponseEntity.ok(fav);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{productoId}")
    public ResponseEntity<?> quitar(@PathVariable Long productoId, @AuthenticationPrincipal Usuario usuario) {
        try {
            System.out.println("🔍 DEBUG: Quitando favorito - UsuarioId: " + usuario.getId() + ", ProductoId: " + productoId);
            favoritoService.quitarFavorito(usuario.getId(), productoId);
            System.out.println("🔍 DEBUG: Favorito quitado exitosamente");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.out.println("🔍 DEBUG: Error al quitar favorito: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error al quitar favorito: " + e.getMessage());
        }
    }

    @GetMapping
    public List<ProductoFavorito> listar(@AuthenticationPrincipal Usuario usuario) {
        return favoritoService.listarFavoritos(usuario.getId());
    }
} 