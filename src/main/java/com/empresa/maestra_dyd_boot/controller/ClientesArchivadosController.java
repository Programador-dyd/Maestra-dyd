package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.service.ClientesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ClientesArchivadosController {

    private final ClientesService clientesService;

    public ClientesArchivadosController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    @GetMapping("/clientes/archivados")
    public String listarArchivados() {
        return "redirect:/clientes?estado=Inactivo";
    }

    @PostMapping("/clientes/{nit}/restaurar")
    public String restaurar(@PathVariable("nit") String nit) {
        clientesService.restaurar(nit);
        return "redirect:/clientes?estado=Inactivo";
    }

}