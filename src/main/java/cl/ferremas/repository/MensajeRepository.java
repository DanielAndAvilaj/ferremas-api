package cl.ferremas.repository;

import cl.ferremas.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    long countByEstado(Mensaje.EstadoMensaje estado);
    List<Mensaje> findByEstado(Mensaje.EstadoMensaje estado);
    long countByEstadoIn(List<Mensaje.EstadoMensaje> estados);
} 