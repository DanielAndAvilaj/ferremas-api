package cl.ferremas.repository;

import cl.ferremas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Producto findByCodigo(String codigo);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByStockLessThan(Integer stock);
}
