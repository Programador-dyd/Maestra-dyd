package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import com.empresa.maestra_dyd_boot.service.ProveedoresService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProveedoresFormularioController {

    private final ProveedoresService proveedoresService;

    public ProveedoresFormularioController(ProveedoresService proveedoresService) {
        this.proveedoresService = proveedoresService;
    }

    @GetMapping("/proveedoresFormulario")
    public String mostrarFormulario(@RequestParam(required = false) String nitProveedor, Model model) {

        Proveedores proveedor;
        boolean esModificar = nitProveedor != null && !nitProveedor.isBlank();

        if (esModificar) {
            proveedor = proveedoresService.buscarPorNit(nitProveedor);
        } else {
            proveedor = new Proveedores();
            proveedor.setEstado("Activo");
            proveedor.setTipoProveedor("PROVEEDOR");
        }

        model.addAttribute("proveedor", proveedor);
        model.addAttribute("esModificar", esModificar);
        model.addAttribute("nitAnterior", esModificar ? nitProveedor : "");

        return "proveedoresFormulario";
    }

    @PostMapping("/proveedoresFormulario")
    public String guardar(@ModelAttribute Proveedores proveedor, @RequestParam String nitAnterior) {

        if (nitAnterior != null && !nitAnterior.isBlank()) {
            proveedoresService.actualizar(nitAnterior, proveedor);
        } else {
            proveedoresService.guardar(proveedor);
        }

        return "redirect:/proveedores";
    }

}