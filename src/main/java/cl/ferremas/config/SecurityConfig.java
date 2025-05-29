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
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir el registro sin autenticación
                .requestMatchers("/api/usuarios/registrar").permitAll()

                // Permitir lectura de productos y precios a ADMIN y USER
                .requestMatchers(HttpMethod.GET, "/api/productos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/precios/**").hasAnyRole("ADMIN", "USER")

                // Permitir escritura solo a ADMIN
                .requestMatchers("/api/productos/**", "/api/precios/**").hasRole("ADMIN")

                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .userDetailsService(usuarioService)
            .httpBasic();

        return http.build();
    }
}
