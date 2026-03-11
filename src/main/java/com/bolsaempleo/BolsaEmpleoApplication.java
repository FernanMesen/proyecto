package com.bolsaempleo;

import com.bolsaempleo.model.Usuario;
import com.bolsaempleo.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BolsaEmpleoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BolsaEmpleoApplication.class, args);
    }

    @Bean
    public CommandLineRunner inicializarAdmin(UsuarioRepository usuarioRepo,
                                              PasswordEncoder encoder) {
        return args -> {
            String correo = "admin@bolsaempleo.local";
            String clave  = "admin123";

            usuarioRepo.findByCorreo(correo).ifPresentOrElse(
                admin -> {
                    admin.setClave(encoder.encode(clave));
                    admin.setActivo(true);
                    usuarioRepo.save(admin);
                },
                () -> {
                    Usuario admin = new Usuario();
                    admin.setCorreo(correo);
                    admin.setClave(encoder.encode(clave));
                    admin.setRol(Usuario.Rol.ADMIN);
                    admin.setActivo(true);
                    usuarioRepo.save(admin);
                }
            );
        };
    }
}