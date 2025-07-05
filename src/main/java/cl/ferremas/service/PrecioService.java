package cl.ferremas.service;

import cl.ferremas.model.Precio;
import cl.ferremas.repository.PrecioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio para la lógica de negocio de precios.
 */
@Service
public class PrecioService {

    private final PrecioRepository precioRepository;

    public PrecioService(PrecioRepository precioRepository) {
        this.precioRepository = precioRepository;
    }

    /**
     * Obtiene los precios de un producto por su código.
     */
    public List<Precio> obtenerPreciosPorCodigoProducto(String codigo) {
        return precioRepository.findByProductoCodigo(codigo);
    }

    /**
     * Guarda un precio.
     */
    public Precio guardarPrecio(Precio precio) {
        return precioRepository.save(precio);
    }
}
