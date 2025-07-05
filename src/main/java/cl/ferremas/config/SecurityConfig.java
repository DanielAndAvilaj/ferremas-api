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

/**
 * Configuración de seguridad para la aplicación Ferremas.
 * Define rutas públicas, protegidas, roles y manejo de sesiones.
 */
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
           // CSRF: Desactivado solo para API REST
           .csrf(csrf -> csrf
               .ignoringRequestMatchers("/api/**")
           )
           // Autorización de rutas
           .authorizeHttpRequests(auth -> auth
               // PÁGINAS WEB PÚBLICAS
               .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/img/**", "/static/**").permitAll()
               // CATÁLOGO PÚBLICO
               .requestMatchers("/catalogo", "/catalogo/**").permitAll()
               .requestMatchers("/sucursales", "/contacto").permitAll()
               // ENDPOINTS DE PRUEBA
               .requestMatchers("/test/**", "/test-simple", "/debug/**").permitAll()
               // CARRITO PÚBLICO (usa sessionId)
               .requestMatchers("/api/carrito/**").permitAll()
               // DASHBOARD Y PÁGINAS DE USUARIO
               .requestMatchers("/dashboard").hasAnyRole("ADMIN", "USER")
               .requestMatchers("/carrito", "/mis-favoritos", "/checkout").hasAnyRole("ADMIN", "USER")
               // ADMIN PANEL
               .requestMatchers("/admin/**").hasRole("ADMIN")
               // API: Registro y login público
               .requestMatchers("/api/usuarios/registrar").permitAll()
               .requestMatchers("/api/auth/**").permitAll()
               // API: Lectura de productos y precios
               .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "USER")
               .requestMatchers(HttpMethod.GET, "/api/precios/**").hasAnyRole("ADMIN", "USER")
               // API: Escritura solo ADMIN
               .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.POST, "/api/precios/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.PUT, "/api/precios/**").hasRole("ADMIN")
               .requestMatchers(HttpMethod.DELETE, "/api/precios/**").hasRole("ADMIN")
               // Cualquier otra petición requiere autenticación
               .anyRequest().authenticated()
           )
           // Login con formulario personalizado
           .formLogin(form -> form
               .loginPage("/login")
               .loginProcessingUrl("/login")
               .successHandler((request, response, authentication) -> {
                   String redirectURL = request.getContextPath();
                   // Redirección post-login según rol o parámetro
                   String requestedURL = request.getParameter("redirect");
                   if (requestedURL != null && !requestedURL.isEmpty() && !requestedURL.startsWith("//")) {
                       redirectURL = requestedURL;
                   } else if (authentication.getAuthorities().stream()
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
           // Logout seguro
           .logout(logout -> logout
               .logoutUrl("/logout")
               .logoutSuccessUrl("/catalogo?logout=success")
               .invalidateHttpSession(true)
               .clearAuthentication(true)
               .deleteCookies("JSESSIONID", "remember-me")
               .permitAll()
           )
           // Gestión de sesión
           .sessionManagement(session -> session
               .maximumSessions(1)
               .expiredUrl("/login?expired=true")
           )
           // Manejo de errores y acceso denegado
           .exceptionHandling(exception -> exception
               .authenticationEntryPoint((request, response, authException) -> {
                   // Redirigir a login si el usuario no está autenticado
                   String requestedURL = request.getRequestURI();
                   // Si es una petición AJAX (API), devolver 401
                   if (requestedURL.startsWith("/api/")) {
                       response.setStatus(401);
                       response.setContentType("application/json");
                       response.getWriter().write("{\"error\":\"Debes iniciar sesión para acceder a este recurso\"}");
                   } else {
                       // Si es una página web, redirigir a login
                       response.sendRedirect("/login?redirect=" + requestedURL);
                   }
               })
               .accessDeniedHandler((request, response, accessDeniedException) -> {
                   // Manejar acceso denegado (403)
                   String requestedURL = request.getRequestURI();
                   if (requestedURL.startsWith("/api/")) {
                       response.setStatus(403);
                       response.setContentType("application/json");
                       response.getWriter().write("{\"error\":\"No tienes permisos para acceder a este recurso\"}");
                   } else {
                       response.sendRedirect("/login?error=access_denied");
                   }
               })
           )
           // UserDetailsService para autenticación
           .userDetailsService(usuarioService);
       
       return http.build();
   }
}