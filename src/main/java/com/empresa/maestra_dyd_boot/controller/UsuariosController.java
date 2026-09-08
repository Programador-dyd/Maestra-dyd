package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.service.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UsuariosController {

    private final PersonaService personaService;

    public UsuariosController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", personaService.listarUsuarios());
        return "usuarios";
    }

    @PostMapping("/usuarios/{identificacion}/eliminar")
    public String eliminar(@PathVariable String identificacion) {
        personaService.eliminar(identificacion);
        return "redirect:/usuarios";
    }

}