package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import com.empresa.maestra_dyd_boot.service.ProveedoresService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class ProveedoresController {

    private final ProveedoresService proveedoresService;

    public ProveedoresController(ProveedoresService proveedoresService) {
        this.proveedoresService = proveedoresService;
    }

    @GetMapping("/proveedores")
    public String listar(
            @RequestParam(required = false) String buscador,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPrograma,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSiesa,
            @RequestParam(required = false) String estadoDocumentacion,
            @RequestParam(defaultValue = "Activo") String estado,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        Page<Proveedores> resultado = proveedoresService.buscarPaginado(buscador, fechaPrograma, fechaSiesa, estadoDocumentacion, estado, pagina);

        model.addAttribute("proveedores", resultado.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("totalRegistros", resultado.getTotalElements());

        model.addAttribute("buscador", buscador == null ? "" : buscador);
        model.addAttribute("fechaPrograma", fechaPrograma);
        model.addAttribute("fechaSiesa", fechaSiesa);
        model.addAttribute("estadoDocumentacion", estadoDocumentacion == null ? "" : estadoDocumentacion);
        model.addAttribute("estado", estado);

        return "proveedores";
    }

    @PostMapping("/proveedores/{nit}/archivar")
    public String archivar(@PathVariable("nit") String nit) {
        proveedoresService.archivar(nit);
        return "redirect:/proveedores";
    }

    @PostMapping("/proveedores/{nit}/restaurar")
    public String restaurar(@PathVariable("nit") String nit) {
        proveedoresService.restaurar(nit);
        return "redirect:/proveedores";
    }

}