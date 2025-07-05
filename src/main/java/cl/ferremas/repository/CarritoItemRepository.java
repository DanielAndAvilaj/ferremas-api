package cl.ferremas.repository;

import cl.ferremas.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
 
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    List<CarritoItem> findBySessionId(String sessionId);
    void deleteBySessionId(String sessionId);
} 