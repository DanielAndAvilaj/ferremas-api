package cl.ferremas.repository;

import cl.ferremas.model.StockSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockSucursalRepository extends JpaRepository<StockSucursal, Long> {
    List<StockSucursal> findBySucursalId(Long sucursalId);
    List<StockSucursal> findByProductoId(Long productoId);
}
