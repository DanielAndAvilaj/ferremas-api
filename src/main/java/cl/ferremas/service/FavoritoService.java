package cl.ferremas.service;

import cl.ferremas.model.Producto;
import cl.ferremas.model.ProductoFavorito;
import cl.ferremas.model.Usuario;
import cl.ferremas.repository.ProductoFavoritoRepository;
import cl.ferremas.repository.ProductoRepository;
import cl.ferremas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoritoService {
    @Autowired
    private ProductoFavoritoRepository favoritoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ProductoFavorito agregarFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        if (favoritoRepository.existsByUsuarioAndProducto(usuario, producto)) {
            throw new RuntimeException("Ya es favorito");
        }
        ProductoFavorito fav = new ProductoFavorito();
        fav.setUsuario(usuario);
        fav.setProducto(producto);
        fav.setFechaCreacion(LocalDateTime.now());
        return favoritoRepository.save(fav);
    }

    @Transactional
    public void quitarFavorito(Long usuarioId, Long productoId) {
        System.out.println("🔍 DEBUG: FavoritoService.quitarFavorito - UsuarioId: " + usuarioId + ", ProductoId: " + productoId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        System.out.println("🔍 DEBUG: Usuario encontrado: " + usuario.getEmail());
        
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        System.out.println("🔍 DEBUG: Producto encontrado: " + producto.getNombre());
        
        boolean existe = favoritoRepository.existsByUsuarioAndProducto(usuario, producto);
        System.out.println("🔍 DEBUG: ¿Existe favorito? " + existe);
        
        if (existe) {
            favoritoRepository.deleteByUsuarioAndProducto(usuario, producto);
            System.out.println("🔍 DEBUG: Favorito eliminado de la base de datos");
        } else {
            System.out.println("🔍 DEBUG: No se encontró el favorito para eliminar");
        }
    }

    public List<ProductoFavorito> listarFavoritos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        return favoritoRepository.findByUsuario(usuario);
    }

    public boolean esFavorito(Long usuarioId, Long productoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();
        Producto producto = productoRepository.findById(productoId).orElseThrow();
        return favoritoRepository.existsByUsuarioAndProducto(usuario, producto);
    }
} 