package cl.ferremas.service;

import cl.ferremas.model.StockSucursal;
import cl.ferremas.repository.StockSucursalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la lógica de negocio de stock por sucursal.
 */
@Service
public class StockSucursalService {

    private final StockSucursalRepository stockSucursalRepository;

    public StockSucursalService(StockSucursalRepository stockSucursalRepository) {
        this.stockSucursalRepository = stockSucursalRepository;
    }

    /**
     * Obtiene todos los registros de stock por sucursal.
     */
    public List<StockSucursal> obtenerTodos() {
        return stockSucursalRepository.findAll();
    }

    /**
     * Busca registros de stock por ID de sucursal.
     */
    public List<StockSucursal> buscarPorSucursal(Long sucursalId) {
        return stockSucursalRepository.findBySucursalId(sucursalId);
    }

    /**
     * Busca registros de stock por ID de producto.
     */
    public List<StockSucursal> buscarPorProducto(Long productoId) {
        return stockSucursalRepository.findByProductoId(productoId);
    }

    /**
     * Guarda un registro de stock por sucursal.
     */
    public StockSucursal guardar(StockSucursal stockSucursal) {
        return stockSucursalRepository.save(stockSucursal);
    }
}
