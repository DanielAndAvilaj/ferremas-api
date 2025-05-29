package cl.ferremas.repository;

import cl.ferremas.model.Precio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrecioRepository extends JpaRepository<Precio, Long> {
    List<Precio> findByProductoCodigo(String codigo);
}
