package cl.ferremas.repository;

import cl.ferremas.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cl.ferremas.model.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email);
    List<Usuario> findByEnabled(boolean enabled);
    Page<Usuario> findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(String nombre, String email, Pageable pageable);
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
    Page<Usuario> findByRolAndNombreContainingIgnoreCase(Rol rol, String nombre, Pageable pageable);
    Page<Usuario> findByRolAndEmailContainingIgnoreCase(Rol rol, String email, Pageable pageable);
}
