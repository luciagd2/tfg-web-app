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


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ServicioAutenticacion servicioAutenticacion;

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

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return servicioAutenticacion; // Tu servicio personalizado que implementa UserDetailsService
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
