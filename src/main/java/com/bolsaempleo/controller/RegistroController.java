package com.bolsaempleo.controller;

import com.bolsaempleo.service.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @GetMapping("/empresa")
    public String formEmpresa() { return "public/registro-empresa"; }

    @PostMapping("/empresa")
    public String registrarEmpresa(@RequestParam String correo,
                                   @RequestParam String clave,
                                   @RequestParam String nombre,
                                   @RequestParam String localizacion,
                                   @RequestParam String telefono,
                                   @RequestParam String descripcion,
                                   Model model) {
        try {
            registroService.registrarEmpresa(correo, clave, nombre, localizacion, telefono, descripcion);
            return "redirect:/login?registroOk=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "public/registro-empresa";
        }
    }

    @GetMapping("/oferente")
    public String formOferente() { return "public/registro-oferente"; }

    @PostMapping("/oferente")
    public String registrarOferente(@RequestParam String correo,
                                    @RequestParam String clave,
                                    @RequestParam String identificacion,
                                    @RequestParam String nombre,
                                    @RequestParam String primerApellido,
                                    @RequestParam String nacionalidad,
                                    @RequestParam String telefono,
                                    @RequestParam String residencia,
                                    Model model) {
        try {
            registroService.registrarOferente(correo, clave, identificacion, nombre,
                primerApellido, nacionalidad, telefono, residencia);
            return "redirect:/login?registroOk=true";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "public/registro-oferente";
        }
    }
}