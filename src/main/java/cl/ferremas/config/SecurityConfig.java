package cl.ferremas.config;
import cl.ferremas.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {
   private final UsuarioService usuarioService;
   
   public SecurityConfig(UsuarioService usuarioService) {
       this.usuarioService = usuarioService;
   }
   
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }
   
   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
       return config.getAuthenticationManager();
   }
   
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
           .csrf(csrf -> csrf
               .ignoringRequestMatchers("/api/**")
           )
           .authorizeHttpRequests(auth -> auth
               // PÁGINAS WEB - Permitir sin autenticación
               .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
               
               // ADMIN PANEL - Solo para ADMIN
               .requestMatchers("/admin/**").hasRole("ADMIN")
               
               // API - Permitir registro y login sin autenticación
               .requestMatchers("/api/usuarios/registrar").permitAll()
               .requestMatchers("/api/auth/**").permitAll()
               
               // API - Permitir lectura productos y precios solo a ADMIN y USER
               .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "USER")
               .requestMatchers(HttpMethod.GET, "/api/precios/**").hasAnyRole("ADMIN", "USER")
               
               // API - Permitir escritura solo a ADMIN
               .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.POST, "/api/precios/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.PUT, "/api/precios/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.DELETE, "/api/precios/**").hasRole("ADMIN")
               
               // Cualquier otra petición requiere autenticación
               .anyRequest().authenticated()
           )
           .formLogin(form -> form
               .loginPage("/login")
               .successHandler((request, response, authentication) -> {
                   String redirectURL = request.getContextPath();
                   
                   if (authentication.getAuthorities().stream()
                           .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                       redirectURL = "/admin";
                   } else {
                       redirectURL = "/dashboard";
                   }
                   
                   response.sendRedirect(redirectURL);
               })
               .failureUrl("/login?error=true")
               .usernameParameter("username")
               .passwordParameter("password")
               .permitAll()
           )
           .logout(logout -> logout
               .logoutUrl("/logout")
               .logoutSuccessUrl("/login?success=true")
               .permitAll()
           )
           .userDetailsService(usuarioService)
           .httpBasic();
           
       return http.build();
   }
}