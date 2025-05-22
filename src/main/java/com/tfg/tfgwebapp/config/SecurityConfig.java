package com.tfg.tfgwebapp.config;

import com.tfg.tfgwebapp.servicios.ServicioAutenticacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Clase de configuración de seguridad para la aplicación.
 * <p>
 * Define las políticas de autenticación, autorización, manejo de sesiones
 * y la integración del servicio personalizado de autenticación.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ServicioAutenticacion servicioAutenticacion;

    /**
     * Configura la cadena de filtros de seguridad de Spring Security.
     *
     * - Desactiva CSRF para facilitar pruebas o APIs sin formularios.
     * - Define las rutas públicas y las que requieren autenticación.
     * - Configura el manejo de sesión (solo crea sesión si es necesario).
     *
     * @param http el objeto HttpSecurity que permite la configuración del acceso web.
     * @return la cadena de filtros de seguridad configurada.
     * @throws Exception si hay algún problema durante la configuración.
     */
    @Bean
    public DefaultSecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/usuarios/registro",
                                         "/api/usuarios/login",
                                         "api/usuarios/perfil",
                                         "/login",
                                         "/*.html",
                                         "/",
                                         "/index.html",
                                         "/css/**",
                                         "/js/**",
                                         "/css-bootstrap/**",
                                         "/js-bootstrap/**",
                                         "/imagenes/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crea sesión solo si es necesario
                )
                //.formLogin(form -> form.disable()) // Desactiva login por formulario
                //.httpBasic(httpBasic -> httpBasic.disable()) // Desactiva auth básica
                .build();
    }

    /**
     * Provee el gestor de autenticación basado en la configuración definida.
     *
     * @param config configuración de autenticación de Spring Boot.
     * @return el {@link AuthenticationManager} usado para autenticar usuarios.
     * @throws Exception si falla la creación del gestor de autenticación.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provee el servicio de detalles de usuario personalizado para Spring Security.
     *
     * @return el servicio que implementa {@link UserDetailsService}.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return servicioAutenticacion; // Servicio personalizado que implementa UserDetailsService
    }

    /**
     * Provee el codificador de contraseñas usando BCrypt.
     * <p>
     * Es necesario para verificar contraseñas encriptadas al hacer login.
     *
     * @return un {@link PasswordEncoder} basado en BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
