package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.service.TipoDocumentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TipoDocumentoController {

    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping("/tipos-documento")
    public String listar(Model model) {
        model.addAttribute("tiposDocumento", tipoDocumentoService.listarTodos());
        return "tipoDocumento";
    }

    @PostMapping("/tipos-documento/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        tipoDocumentoService.eliminar(id);
        return "redirect:/tipos-documento";
    }

}