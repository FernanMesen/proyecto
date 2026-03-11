package com.bolsaempleo.controller;

import com.bolsaempleo.model.*;
import com.bolsaempleo.repository.*;
import com.bolsaempleo.service.ReporteService;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EmpresaRepository empresaRepo;
    private final OferenteRepository oferenteRepo;
    private final UsuarioRepository usuarioRepo;
    private final CaracteristicaRepository carRepo;
    private final ReporteService reporteService;

    public AdminController(EmpresaRepository empresaRepo,
                           OferenteRepository oferenteRepo,
                           UsuarioRepository usuarioRepo,
                           CaracteristicaRepository carRepo,
                           ReporteService reporteService) {
        this.empresaRepo    = empresaRepo;
        this.oferenteRepo   = oferenteRepo;
        this.usuarioRepo    = usuarioRepo;
        this.carRepo        = carRepo;
        this.reporteService = reporteService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("empresasPendientes",  empresaRepo.findByUsuarioActivoFalse().size());
        model.addAttribute("oferentesPendientes", oferenteRepo.findByUsuarioActivoFalse().size());
        return "admin/dashboard";
    }

    @GetMapping("/empresas/pendientes")
    public String empresasPendientes(Model model) {
        model.addAttribute("empresas", empresaRepo.findByUsuarioActivoFalse());
        return "admin/empresas-pendientes";
    }

    @PostMapping("/empresas/{id}/aprobar")
    public String aprobarEmpresa(@PathVariable Long id) {
        Empresa e = empresaRepo.findById(id).orElseThrow();
        Usuario u = e.getUsuario();
        u.setActivo(true);
        usuarioRepo.save(u);
        return "redirect:/admin/empresas/pendientes?aprobado=" + u.getCorreo();
    }

    @GetMapping("/oferentes/pendientes")
    public String oferentesPendientes(Model model) {
        model.addAttribute("oferentes", oferenteRepo.findByUsuarioActivoFalse());
        return "admin/oferentes-pendientes";
    }

    @PostMapping("/oferentes/{id}/aprobar")
    public String aprobarOferente(@PathVariable Long id) {
        Oferente o = oferenteRepo.findById(id).orElseThrow();
        Usuario u = o.getUsuario();
        u.setActivo(true);
        usuarioRepo.save(u);
        return "redirect:/admin/oferentes/pendientes?aprobado=" + u.getCorreo();
    }

    @GetMapping("/caracteristicas")
    public String caracteristicas(@RequestParam(required = false) Long actualId, Model model) {
        model.addAttribute("raices",              carRepo.findByPadreIsNull());
        model.addAttribute("todasCaracteristicas", carRepo.findAll());
        if (actualId != null) {
            Caracteristica actual = carRepo.findById(actualId).orElseThrow();
            model.addAttribute("actual", actual);
            model.addAttribute("hijos",  actual.getHijos());
        } else {
            model.addAttribute("hijos", carRepo.findByPadreIsNull());
        }
        return "admin/caracteristicas";
    }

    @PostMapping("/caracteristicas/crear")
    public String crearCaracteristica(@RequestParam String nombre,
                                      @RequestParam(required = false) Long padreId) {
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        if (padreId != null)
            c.setPadre(carRepo.findById(padreId).orElseThrow());
        carRepo.save(c);
        return padreId != null
            ? "redirect:/admin/caracteristicas?actualId=" + padreId
            : "redirect:/admin/caracteristicas";
    }

    @GetMapping("/reportes")
    public String formReportes() { return "admin/reportes"; }

    @GetMapping("/reportes/pdf")
    public ResponseEntity<byte[]> generarReporte(@RequestParam int mes,
                                                  @RequestParam int anio) {
        try {
            byte[] pdf = reporteService.reportePuestosPorMes(mes, anio);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"reporte-" + mes + "-" + anio + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
