package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.TipoDocumento;
import com.empresa.maestra_dyd_boot.service.TipoDocumentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TipoDocumentoFormularioController {

    private final TipoDocumentoService tipoDocumentoService;

    public TipoDocumentoFormularioController(TipoDocumentoService tipoDocumentoService) {
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping("/tipoDocumentoFormulario")
    public String mostrarFormulario(@RequestParam(required = false) Long id, Model model) {

        TipoDocumento tipoDocumento;
        boolean esModificar = id != null;

        if (esModificar) {
            tipoDocumento = tipoDocumentoService.buscarPorId(id);
        } else {
            tipoDocumento = new TipoDocumento();
        }

        model.addAttribute("tipoDocumento", tipoDocumento);
        model.addAttribute("esModificar", esModificar);

        return "tipoDocumentoFormulario";
    }

    @PostMapping("/tipoDocumentoFormulario")
    public String guardar(@ModelAttribute TipoDocumento tipoDocumento) {
        tipoDocumentoService.guardar(tipoDocumento);
        return "redirect:/tipos-documento";
    }

}