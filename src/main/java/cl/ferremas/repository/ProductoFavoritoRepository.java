package cl.ferremas.repository;

import cl.ferremas.model.ProductoFavorito;
import cl.ferremas.model.Usuario;
import cl.ferremas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoFavoritoRepository extends JpaRepository<ProductoFavorito, Long> {
    List<ProductoFavorito> findByUsuario(Usuario usuario);
    List<ProductoFavorito> findByUsuarioId(Long usuarioId);
    Optional<ProductoFavorito> findByUsuarioAndProducto(Usuario usuario, Producto producto);
    boolean existsByUsuarioAndProducto(Usuario usuario, Producto producto);
    void deleteByUsuarioAndProducto(Usuario usuario, Producto producto);
} 