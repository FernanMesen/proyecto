package com.bolsaempleo.controller;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import com.bolsaempleo.service.PuestoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/empresa")
public class EmpresaController {

    private final EmpresaRepository empresaRepo;
    private final PuestoRepository puestoRepo;
    private final PuestoService puestoService;
    private final CaracteristicaRepository carRepo;
    private final OferenteRepository oferenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final AplicacionRepository aplicacionRepo;

    public EmpresaController(EmpresaRepository empresaRepo,
                             PuestoRepository puestoRepo,
                             PuestoService puestoService,
                             CaracteristicaRepository carRepo,
                             OferenteRepository oferenteRepo,
                             UsuarioRepository usuarioRepo,
                             AplicacionRepository aplicacionRepo) {
        this.empresaRepo    = empresaRepo;
        this.puestoRepo     = puestoRepo;
        this.puestoService  = puestoService;
        this.carRepo        = carRepo;
        this.oferenteRepo   = oferenteRepo;
        this.usuarioRepo    = usuarioRepo;
        this.aplicacionRepo = aplicacionRepo;
    }

    private Empresa getEmpresa(UserDetails ud) {
        Usuario u = usuarioRepo.findByCorreo(ud.getUsername()).orElseThrow();
        return empresaRepo.findByUsuarioId(u.getId()).orElseThrow();
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("empresa", getEmpresa(ud));
        return "empresa/dashboard";
    }

    @GetMapping("/puestos")
    public String misPuestos(@AuthenticationPrincipal UserDetails ud, Model model) {
        Empresa empresa = getEmpresa(ud);
        model.addAttribute("puestos", puestoRepo.findByEmpresa(empresa));
        model.addAttribute("empresa", empresa);
        return "empresa/puestos";
    }

    @GetMapping("/puestos/nuevo")
    public String formNuevoPuesto(@AuthenticationPrincipal UserDetails ud, Model model) {
        model.addAttribute("caracteristicas", carRepo.findAll());
        model.addAttribute("empresa", getEmpresa(ud));
        return "empresa/nuevo-puesto";
    }

    @PostMapping("/puestos/nuevo")
    public String guardarPuesto(@AuthenticationPrincipal UserDetails ud,
                                @RequestParam String descripcion,
                                @RequestParam(required = false) BigDecimal salario,
                                @RequestParam Puesto.TipoPuesto tipo,
                                HttpServletRequest request,
                                Model model) {
        try {
            List<Long> caracteristicaIds = new ArrayList<>();
            List<Integer> niveles = new ArrayList<>();

            for (Caracteristica car : carRepo.findAll()) {
                String sel = request.getParameter("sel_" + car.getId());
                if (sel != null) {
                    String niv = request.getParameter("niv_" + car.getId());
                    caracteristicaIds.add(car.getId());
                    niveles.add(niv != null ? Integer.parseInt(niv) : 1);
                }
            }

            Usuario u = usuarioRepo.findByCorreo(ud.getUsername()).orElseThrow();
            puestoService.publicarPuesto(u.getId(), descripcion, salario, tipo,
                    caracteristicaIds, niveles, carRepo);
            return "redirect:/empresa/puestos?ok=true";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("caracteristicas", carRepo.findAll());
            model.addAttribute("empresa", getEmpresa(ud));
            return "empresa/nuevo-puesto";
        }
    }

    @PostMapping("/puestos/{id}/desactivar")
    public String desactivar(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails ud) {
        Usuario u = usuarioRepo.findByCorreo(ud.getUsername()).orElseThrow();
        puestoService.desactivarPuesto(id, u.getId());
        return "redirect:/empresa/puestos";
    }

    @PostMapping("/puestos/{id}/activar")
    public String activar(@PathVariable Long id,
                          @AuthenticationPrincipal UserDetails ud) {
        Usuario u = usuarioRepo.findByCorreo(ud.getUsername()).orElseThrow();
        puestoService.activarPuesto(id, u.getId());
        return "redirect:/empresa/puestos";
    }

    @GetMapping("/candidatos/buscar")
    public String buscarCandidatos(@RequestParam Long puestoId,
                                   @AuthenticationPrincipal UserDetails ud,
                                   Model model) {
        model.addAttribute("puesto",     puestoRepo.findById(puestoId).orElseThrow());
        model.addAttribute("candidatos", puestoService.buscarCandidatos(puestoId));
        model.addAttribute("empresa",    getEmpresa(ud));
        return "empresa/candidatos";
    }

    @GetMapping("/puestos/{id}/aplicaciones")
    public String verAplicaciones(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails ud,
                                  Model model) {
        Empresa empresa = getEmpresa(ud);
        Puesto puesto = puestoRepo.findById(id).orElseThrow();
        if (!puesto.getEmpresa().getId().equals(empresa.getId())) {
            return "redirect:/empresa/puestos";
        }
        model.addAttribute("puesto",       puesto);
        model.addAttribute("aplicaciones", aplicacionRepo.findByPuestoId(id));
        model.addAttribute("empresa",      empresa);
        return "empresa/aplicaciones";
    }

    @GetMapping("/candidatos/{id}")
    public String detalleCandidato(@PathVariable Long id,
                                   @AuthenticationPrincipal UserDetails ud,
                                   Model model) {
        model.addAttribute("oferente", oferenteRepo.findById(id).orElseThrow());
        model.addAttribute("empresa",  getEmpresa(ud));
        return "empresa/detalle-candidato";
    }
}