package cl.ferremas.model;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.time.LocalDateTime;

@Entity
public class Usuario implements UserDetails {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   private String username;
   private String nombre;
   private String email;
   private String password;
   @Enumerated(EnumType.STRING)
   private Rol rol;
   @Column(nullable = false)
   private LocalDateTime fechaRegistro;
   @Column(nullable = false)
   private boolean enabled = true;
   
   // Getters y Setters
   public Long getId() {
       return id;
   }
   public void setId(Long id) {
       this.id = id;
   }
   public String getUsername() {
       return username;
   }
   public void setUsername(String username) {
       this.username = username;
   }
   
   public String getNombre() {
       return nombre;
   }
   public void setNombre(String nombre) {
       this.nombre = nombre;
   }
   
   public String getEmail() {
       return email;
   }
   public void setEmail(String email) {
       this.email = email;
   }
   
   public String getPassword() {
       return password;
   }
   public void setPassword(String password) {
       this.password = password;
   }
   public Rol getRol() {
       return rol;
   }
   public void setRol(Rol rol) {
       this.rol = rol;
   }
   
   public LocalDateTime getFechaRegistro() {
       return fechaRegistro;
   }
   public void setFechaRegistro(LocalDateTime fechaRegistro) {
       this.fechaRegistro = fechaRegistro;
   }
   
   public boolean isEnabled() {
       return enabled;
   }
   public void setEnabled(boolean enabled) {
       this.enabled = enabled;
   }
   
   // Métodos de la interfaz UserDetails
   @Override
   public Collection<? extends GrantedAuthority> getAuthorities() {
       return List.of(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
   }
   @Override
   public boolean isAccountNonExpired() {
       return true;
   }
   @Override
   public boolean isAccountNonLocked() {
       return true;
   }
   @Override
   public boolean isCredentialsNonExpired() {
       return true;
   }
}