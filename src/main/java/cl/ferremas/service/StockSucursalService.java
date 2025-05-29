package cl.ferremas.service;

import cl.ferremas.model.StockSucursal;
import cl.ferremas.repository.StockSucursalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockSucursalService {

    private final StockSucursalRepository stockSucursalRepository;

    public StockSucursalService(StockSucursalRepository stockSucursalRepository) {
        this.stockSucursalRepository = stockSucursalRepository;
    }

    public List<StockSucursal> obtenerTodos() {
        return stockSucursalRepository.findAll();
    }

    public List<StockSucursal> buscarPorSucursal(Long sucursalId) {
        return stockSucursalRepository.findBySucursalId(sucursalId);
    }

    public List<StockSucursal> buscarPorProducto(Long productoId) {
        return stockSucursalRepository.findByProductoId(productoId);
    }

    public StockSucursal guardar(StockSucursal stockSucursal) {
        return stockSucursalRepository.save(stockSucursal);
    }
}
