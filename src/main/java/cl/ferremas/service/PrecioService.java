package cl.ferremas.service;

import cl.ferremas.model.Precio;
import cl.ferremas.repository.PrecioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrecioService {

    private final PrecioRepository precioRepository;

    public PrecioService(PrecioRepository precioRepository) {
        this.precioRepository = precioRepository;
    }

    public List<Precio> obtenerPreciosPorCodigoProducto(String codigo) {
        return precioRepository.findByProductoCodigo(codigo);
    }

    public Precio guardarPrecio(Precio precio) {
        return precioRepository.save(precio);
    }
}
