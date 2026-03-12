package com.bolsaempleo.controller;

import com.bolsaempleo.model.Puesto;
import com.bolsaempleo.repository.CaracteristicaRepository;
import com.bolsaempleo.service.PuestoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PublicController {

    private final PuestoService puestoService;
    private final CaracteristicaRepository carRepo;

    public PublicController(PuestoService puestoService, CaracteristicaRepository carRepo) {
        this.puestoService = puestoService;
        this.carRepo       = carRepo;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("puestos", puestoService.ultimos5Publicos());
        return "public/inicio";
    }

    @GetMapping("/puestos/buscar")
    public String buscar(@RequestParam(value = "caracteristicaIds", required = false) List<Long> caracteristicaIds,
                         Authentication auth,
                         Model model) {

        boolean puedeVerPrivados = auth != null && auth.isAuthenticated()
            && auth.getAuthorities().stream()
               .anyMatch(a -> a.getAuthority().equals("ROLE_OFERENTE"));

        model.addAttribute("raices", carRepo.findByPadreIsNull());
        model.addAttribute("caracteristicaIds", caracteristicaIds);

        if (caracteristicaIds != null && !caracteristicaIds.isEmpty()) {
            List<Puesto> resultados = puestoService.buscarPorCaracteristicas(caracteristicaIds, puedeVerPrivados);
            model.addAttribute("resultados", resultados);
        } else {
            List<Puesto> resultados = puestoService.buscarTodos(puedeVerPrivados);
            model.addAttribute("resultados", resultados);
        }
        return "public/buscar";
    }
}