package cl.ferremas.service;
import cl.ferremas.model.Usuario;
import cl.ferremas.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import cl.ferremas.model.Rol;

/**
 * Servicio para la lógica de negocio de usuarios y autenticación.
 */
@Service
public class UsuarioService implements UserDetailsService {
   private final UsuarioRepository usuarioRepository;
   private final PasswordEncoder passwordEncoder;
   
   public UsuarioService(UsuarioRepository usuarioRepository, @Lazy PasswordEncoder passwordEncoder) {
       this.usuarioRepository = usuarioRepository;
       this.passwordEncoder = passwordEncoder;
   }
   
   /**
    * Carga un usuario por username o email (para Spring Security).
    */
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // Buscar por email O por username
       Usuario usuario = usuarioRepository.findByEmail(username)
               .orElse(usuarioRepository.findByUsername(username)
                       .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username)));
       
       return usuario;
   }
   
   /**
    * Registra un nuevo usuario.
    */
   public Usuario registrarUsuario(Usuario usuario) {
       return usuarioRepository.save(usuario);
   }
   
   /**
    * Busca un usuario por username.
    */
   public Optional<Usuario> buscarPorUsername(String username) {
       return usuarioRepository.findByUsername(username);
   }
   
   /**
    * Busca un usuario por username (excepción si no existe).
    */
   public Usuario findByUsername(String username) {
       return usuarioRepository.findByUsername(username)
           .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
   }
   
   /**
    * Verifica si existe un email registrado.
    */
   public boolean existeEmail(String email) {
       return usuarioRepository.existsByEmail(email);
   }
   
   /**
    * Busca un usuario por email (excepción si no existe).
    */
   public Usuario buscarPorEmail(String email) {
       return usuarioRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
   }
   
   /**
    * Crea un usuario con codificación de contraseña y fecha de registro.
    */
   public Usuario crearUsuario(Usuario usuario) {
       usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
       usuario.setFechaRegistro(java.time.LocalDateTime.now());
       return usuarioRepository.save(usuario);
   }
   
   /**
    * Cuenta los usuarios registrados hoy.
    */
   public long contarUsuariosHoy() {
       LocalDate hoy = LocalDate.now();
       return usuarioRepository.findAll().stream()
           .filter(u -> u.getFechaRegistro() != null && u.getFechaRegistro().toLocalDate().isEqual(hoy))
           .count();
   }

   /**
    * Cuenta los usuarios registrados en la semana actual.
    */
   public long contarUsuariosSemana() {
       LocalDate hoy = LocalDate.now();
       WeekFields weekFields = WeekFields.of(Locale.getDefault());
       int semanaActual = hoy.get(weekFields.weekOfWeekBasedYear());
       int anioActual = hoy.getYear();
       return usuarioRepository.findAll().stream()
           .filter(u -> u.getFechaRegistro() != null &&
               u.getFechaRegistro().getYear() == anioActual &&
               u.getFechaRegistro().get(weekFields.weekOfWeekBasedYear()) == semanaActual)
           .count();
   }

   /**
    * Cuenta el total de usuarios registrados.
    */
   public long contarUsuariosTotal() {
       return usuarioRepository.count();
   }

   /**
    * Obtiene todos los usuarios.
    */
   public List<Usuario> obtenerTodos() {
       return usuarioRepository.findAll();
   }

   /**
    * Busca usuarios por nombre o email (contiene, ignore case).
    */
   public List<Usuario> buscarPorNombreOEmail(String q) {
       return usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(q, q);
   }

   /**
    * Obtiene un usuario por ID (excepción si no existe).
    */
   public Usuario obtenerPorId(Long id) {
       return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
   }

   public void cambiarRol(Long id, cl.ferremas.model.Rol rol) {
       Usuario u = obtenerPorId(id);
       u.setRol(rol);
       usuarioRepository.save(u);
   }

   public void activarUsuario(Long id) {
       Usuario u = obtenerPorId(id);
       u.setEnabled(true);
       usuarioRepository.save(u);
   }

   public void desactivarUsuario(Long id) {
       Usuario u = obtenerPorId(id);
       u.setEnabled(false);
       usuarioRepository.save(u);
   }
   
   public void actualizarContraseña(Long id, String nuevaContraseña) {
       Usuario u = obtenerPorId(id);
       u.setPassword(passwordEncoder.encode(nuevaContraseña));
       // Asegurar que la fecha de registro esté presente
       if (u.getFechaRegistro() == null) {
           u.setFechaRegistro(java.time.LocalDateTime.now());
       }
       usuarioRepository.save(u);
   }

   public void eliminarUsuario(Long id) {
       usuarioRepository.deleteById(id);
   }

   public Page<Usuario> buscarUsuarios(String busqueda, Rol rol, Pageable pageable) {
       if ((busqueda == null || busqueda.isBlank()) && rol == null) {
           return usuarioRepository.findAll(pageable);
       } else if (rol != null && (busqueda == null || busqueda.isBlank())) {
           return usuarioRepository.findByRol(rol, pageable);
       } else if (rol == null) {
           return usuarioRepository.findByNombreContainingIgnoreCaseOrEmailContainingIgnoreCase(busqueda, busqueda, pageable);
       } else {
           // Buscar por rol y nombre/email usando métodos separados
           Page<Usuario> porNombre = usuarioRepository.findByRolAndNombreContainingIgnoreCase(rol, busqueda, pageable);
           Page<Usuario> porEmail = usuarioRepository.findByRolAndEmailContainingIgnoreCase(rol, busqueda, pageable);
           
           // Combinar resultados (esto es una implementación simple, podría mejorarse)
           if (porNombre.getTotalElements() > 0) {
               return porNombre;
           } else {
               return porEmail;
           }
       }
   }

   public void actualizarUsuario(Long id, Usuario usuarioEditado) {
       Usuario usuario = obtenerPorId(id);
       usuario.setNombre(usuarioEditado.getNombre());
       usuario.setEmail(usuarioEditado.getEmail());
       usuario.setRol(usuarioEditado.getRol());
       usuario.setEnabled(usuarioEditado.isEnabled());
       usuarioRepository.save(usuario);
   }
}