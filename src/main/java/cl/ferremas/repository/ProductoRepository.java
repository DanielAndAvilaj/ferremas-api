package cl.ferremas.repository;

import cl.ferremas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Producto findByCodigo(String codigo);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoria(String categoria);

    List<Producto> findByStockLessThan(Integer stock);
    
    /**
     * Obtiene un producto con sus precios cargados
     */
    @Query("SELECT DISTINCT p FROM Producto p " +
           "LEFT JOIN FETCH p.precios pr " +
           "WHERE p.id = :id " +
           "ORDER BY pr.fecha DESC")
    Optional<Producto> findByIdWithPrecios(@Param("id") Long id);
    
    /**
     * Obtiene un producto con sus stocks por sucursal cargados
     */
    @Query("SELECT DISTINCT p FROM Producto p " +
           "LEFT JOIN FETCH p.stocksSucursal ss " +
           "LEFT JOIN FETCH ss.sucursal " +
           "WHERE p.id = :id")
    Optional<Producto> findByIdWithStocksSucursal(@Param("id") Long id);
}
