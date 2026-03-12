package com.bolsaempleo.controller;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Controller
public class AplicacionController {

    private final PuestoRepository puestoRepo;
    private final OferenteRepository oferenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final AplicacionRepository aplicacionRepo;

    @Value("${app.cv.upload-dir:./uploads/cv}")
    private String uploadDir;

    public AplicacionController(PuestoRepository puestoRepo,
                                OferenteRepository oferenteRepo,
                                UsuarioRepository usuarioRepo,
                                AplicacionRepository aplicacionRepo) {
        this.puestoRepo     = puestoRepo;
        this.oferenteRepo   = oferenteRepo;
        this.usuarioRepo    = usuarioRepo;
        this.aplicacionRepo = aplicacionRepo;
    }

    @GetMapping("/puestos/{id}/aplicar")
    public String formularioAplicar(@PathVariable Long id,
                                    Authentication auth,
                                    Model model) {

        Puesto puesto = puestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        boolean esOferente = esOferente(auth);

        if (puesto.getTipo() == Puesto.TipoPuesto.PRIVADO && !esOferente) {
            return "redirect:/login";
        }

        boolean yaAplico = false;
        if (esOferente) {
            Usuario u = usuarioRepo.findByCorreo(auth.getName()).orElseThrow();
            Oferente oferente = oferenteRepo.findByUsuarioId(u.getId()).orElseThrow();
            yaAplico = aplicacionRepo.existsByPuestoAndOferente(puesto, oferente);
        }

        model.addAttribute("puesto", puesto);
        model.addAttribute("esOferente", esOferente);
        model.addAttribute("yaAplico", yaAplico);
        return "public/aplicar";
    }

    @PostMapping("/puestos/{id}/aplicar")
    public String procesarAplicacion(@PathVariable Long id,
                                     @RequestParam(required = false) String nombre,
                                     @RequestParam(required = false) String correo,
                                     @RequestParam(required = false) String telefono,
                                     @RequestParam(required = false) String mensaje,
                                     @RequestParam(required = false) MultipartFile cv,
                                     Authentication auth) throws IOException {

        Puesto puesto = puestoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Puesto no encontrado"));

        boolean esOferente = esOferente(auth);

        if (puesto.getTipo() == Puesto.TipoPuesto.PRIVADO && !esOferente) {
            return "redirect:/login";
        }

        Aplicacion aplicacion = new Aplicacion();
        aplicacion.setPuesto(puesto);
        aplicacion.setMensaje(mensaje);

        if (esOferente) {
            Usuario u = usuarioRepo.findByCorreo(auth.getName()).orElseThrow();
            Oferente oferente = oferenteRepo.findByUsuarioId(u.getId()).orElseThrow();
            if (!aplicacionRepo.existsByPuestoAndOferente(puesto, oferente)) {
                aplicacion.setOferente(oferente);
                aplicacionRepo.save(aplicacion);
            }
        } else {
            aplicacion.setNombreInvitado(nombre);
            aplicacion.setCorreoInvitado(correo);
            aplicacion.setTelefonoInvitado(telefono);

            if (cv != null && !cv.isEmpty()) {
                String nombreArchivo = UUID.randomUUID() + ".pdf";
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);
                Files.copy(cv.getInputStream(), dir.resolve(nombreArchivo),
                           StandardCopyOption.REPLACE_EXISTING);
                aplicacion.setCvInvitado(nombreArchivo);
            }

            aplicacionRepo.save(aplicacion);
        }

        return "redirect:/puestos/" + id + "/aplicar?exito=true";
    }

    private boolean esOferente(Authentication auth) {
        return auth != null && auth.isAuthenticated()
            && auth.getAuthorities().stream()
               .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));
    }
}