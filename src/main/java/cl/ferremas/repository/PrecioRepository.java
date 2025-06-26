package cl.ferremas.repository;

import cl.ferremas.model.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrecioRepository extends JpaRepository<Precio, Long> {
    
    // ✅ MÉTODO QUE FALTABA: Buscar precios por producto ordenados por fecha descendente
    @Query("SELECT p FROM Precio p WHERE p.producto.id = :productoId ORDER BY p.fecha DESC")
    List<Precio> findByProductoIdOrderByFechaDesc(@Param("productoId") Long productoId);
    
    // ✅ MÉTODO ALTERNATIVO: Buscar por producto directamente
    List<Precio> findByProductoOrderByFechaDesc(cl.ferremas.model.Producto producto);
    
    // ✅ MÉTODO ÚTIL: Obtener el último precio de un producto
    @Query("SELECT p FROM Precio p WHERE p.producto.id = :productoId ORDER BY p.fecha DESC LIMIT 1")
    Precio findLatestByProductoId(@Param("productoId") Long productoId);
    
    // ✅ MÉTODO ÚTIL: Contar precios de un producto
    @Query("SELECT COUNT(p) FROM Precio p WHERE p.producto.id = :productoId")
    Long countByProductoId(@Param("productoId") Long productoId);
    
    // ✅ MÉTODO NECESARIO PARA PrecioService: Buscar precios por código de producto
    @Query("SELECT p FROM Precio p WHERE p.producto.codigo = :codigo ORDER BY p.fecha DESC")
    List<Precio> findByProductoCodigo(@Param("codigo") String codigo);
    
    // ✅ MÉTODO ADICIONAL: Buscar último precio por código de producto
    @Query("SELECT p FROM Precio p WHERE p.producto.codigo = :codigo ORDER BY p.fecha DESC LIMIT 1")
    Precio findLatestByProductoCodigo(@Param("codigo") String codigo);
}