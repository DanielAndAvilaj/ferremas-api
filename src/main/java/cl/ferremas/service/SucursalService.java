package cl.ferremas.service;

import cl.ferremas.model.Sucursal;
import cl.ferremas.repository.SucursalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la lógica de negocio de sucursales.
 */
@Service
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalService(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Obtiene todas las sucursales.
     */
    public List<Sucursal> obtenerTodas() {
        return sucursalRepository.findAll();
    }

    /**
     * Guarda una sucursal.
     */
    public Sucursal guardar(Sucursal sucursal) {
        return sucursalRepository.save(sucursal);
    }
}
