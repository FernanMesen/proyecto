package com.bolsaempleo.config;

import com.bolsaempleo.model.Usuario;
import com.bolsaempleo.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepo) {
        return correo -> {
            Usuario u = usuarioRepo.findByCorreo(correo)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

            return new org.springframework.security.core.userdetails.User(
                    u.getCorreo(),
                    u.getClave(),
                    u.isActivo(),
                    true, true, true,
                    List.of(new SimpleGrantedAuthority("ROLE_" + u.getRol().name()))
            );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/puestos/*/aplicar")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/puestos/buscar", "/puestos/*/aplicar",
                                "/registro/**", "/login",
                                "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/empresa/**").hasRole("EMPRESA")
                        .requestMatchers("/oferente/**").hasRole("OFERENTE")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(loginSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return (request, response, authentication) -> {
            String rol = authentication.getAuthorities().iterator().next().getAuthority();
            switch (rol) {
                case "ROLE_EMPRESA"  -> response.sendRedirect("/empresa/dashboard");
                case "ROLE_OFERENTE" -> response.sendRedirect("/oferente/dashboard");
                case "ROLE_ADMIN"    -> response.sendRedirect("/admin/dashboard");
                default              -> response.sendRedirect("/");
            }
        };
    }
}