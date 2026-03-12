package com.bolsaempleo.controller;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/oferente")
public class OferenteController {

    private final OferenteRepository oferenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final HabilidadRepository habilidadRepo;
    private final CaracteristicaRepository carRepo;

    @Value("${app.cv.upload-dir}")
    private String uploadDir;

    public OferenteController(OferenteRepository oferenteRepo,
                              UsuarioRepository usuarioRepo,
                              HabilidadRepository habilidadRepo,
                              CaracteristicaRepository carRepo) {
        this.oferenteRepo = oferenteRepo;
        this.usuarioRepo  = usuarioRepo;
        this.habilidadRepo = habilidadRepo;
        this.carRepo      = carRepo;
    }

    private Oferente getOferente(UserDetails ud) {
        Usuario u = usuarioRepo.findByCorreo(ud.getUsername()).orElseThrow();
        return oferenteRepo.findByUsuarioId(u.getId()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("oferente", getOferente(ud));
        return "oferente/dashboard";
    }

    @GetMapping("/habilidades")
    public String misHabilidades(@AuthenticationPrincipal UserDetails ud, Model model) {
        Oferente o = getOferente(ud);
        model.addAttribute("oferente",           o);
        model.addAttribute("habilidades",         habilidadRepo.findByOferente(o));
        model.addAttribute("todasCaracteristicas", carRepo.findByPadreIsNotNull());
        model.addAttribute("raices",              carRepo.findByPadreIsNull());
        return "oferente/habilidades";
    }

    @PostMapping("/habilidades/agregar")
    public String agregarHabilidad(@AuthenticationPrincipal UserDetails ud,
                                   @RequestParam Long caracteristicaId,
                                   @RequestParam int nivel) {
        Oferente o = getOferente(ud);
        Caracteristica c = carRepo.findById(caracteristicaId).orElseThrow();

        Optional<Habilidad> existente =
            habilidadRepo.findByOferenteAndCaracteristicaId(o, caracteristicaId);
        Habilidad h = existente.orElse(new Habilidad());
        h.setOferente(o);
        h.setCaracteristica(c);
        h.setNivel(nivel);
        habilidadRepo.save(h);
        return "redirect:/oferente/habilidades";
    }

    @GetMapping("/cv")
    public String formCV(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("oferente", getOferente(ud));
        return "oferente/cv";
    }

    @PostMapping("/cv")
    public String subirCV(@AuthenticationPrincipal UserDetails ud,
                          @RequestParam("archivo") MultipartFile archivo,
                          Model model) {
        if (archivo.isEmpty() || !esPdf(archivo)) {
            model.addAttribute("error", "Solo se permiten archivos PDF.");
            model.addAttribute("oferente", getOferente(ud));
            return "oferente/cv";
        }
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String filename = UUID.randomUUID() + ".pdf";
            Files.copy(archivo.getInputStream(), dir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
            Oferente o = getOferente(ud);
            o.setCvPath(filename);
            oferenteRepo.save(o);
            return "redirect:/oferente/dashboard?cvOk=true";
        } catch (IOException ex) {
            model.addAttribute("error", "Error al subir: " + ex.getMessage());
            model.addAttribute("oferente", getOferente(ud));
            return "oferente/cv";
        }
    }

    private boolean esPdf(MultipartFile f) {
        return "application/pdf".equals(f.getContentType()) ||
               (f.getOriginalFilename() != null && f.getOriginalFilename().endsWith(".pdf"));
    }
}