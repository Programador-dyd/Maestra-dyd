package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Acreedores;
import com.empresa.maestra_dyd_boot.service.AcreedoresService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AcreedoresFormularioController {

    private final AcreedoresService acreedoresService;

    public AcreedoresFormularioController(AcreedoresService acreedoresService) {
        this.acreedoresService = acreedoresService;
    }

    @GetMapping("/acreedoresFormulario")
    public String mostrarFormulario(@RequestParam(required = false) String nitAcreedor, Model model) {

        Acreedores acreedor;
        boolean esModificar = nitAcreedor != null && !nitAcreedor.isBlank();

        if (esModificar) {
            acreedor = acreedoresService.buscarPorNit(nitAcreedor);
        } else {
            acreedor = new Acreedores();
            acreedor.setEstado("Activo");
            acreedor.setTipoAcreedor("ACREEDOR");
        }

        model.addAttribute("acreedor", acreedor);
        model.addAttribute("esModificar", esModificar);
        model.addAttribute("nitAnterior", esModificar ? nitAcreedor : "");

        return "acreedoresFormulario";
    }

    @PostMapping("/acreedoresFormulario")
    public String guardar(@ModelAttribute Acreedores acreedor, @RequestParam String nitAnterior) {

        if (nitAnterior != null && !nitAnterior.isBlank()) {
            acreedoresService.actualizar(nitAnterior, acreedor);
        } else {
            acreedoresService.guardar(acreedor);
        }

        return "redirect:/acreedores";
    }

}