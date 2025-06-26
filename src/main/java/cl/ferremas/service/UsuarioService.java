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

@Service
public class UsuarioService implements UserDetailsService {
   private final UsuarioRepository usuarioRepository;
   private final PasswordEncoder passwordEncoder;
   
   public UsuarioService(UsuarioRepository usuarioRepository, @Lazy PasswordEncoder passwordEncoder) {
       this.usuarioRepository = usuarioRepository;
       this.passwordEncoder = passwordEncoder;
   }
   
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       // Buscar por email O por username
       Usuario usuario = usuarioRepository.findByEmail(username)
               .orElse(usuarioRepository.findByUsername(username)
                       .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username)));
       
       return usuario;
   }
   
   public Usuario registrarUsuario(Usuario usuario) {
       return usuarioRepository.save(usuario);
   }
   
   public Optional<Usuario> buscarPorUsername(String username) {
       return usuarioRepository.findByUsername(username);
   }
   
   public Usuario findByUsername(String username) {
       return usuarioRepository.findByUsername(username)
           .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
   }
   
   public boolean existeEmail(String email) {
       return usuarioRepository.existsByEmail(email);
   }
   
   public Usuario buscarPorEmail(String email) {
       return usuarioRepository.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
   }
   
   public Usuario crearUsuario(Usuario usuario) {
       usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
       return usuarioRepository.save(usuario);
   }
}