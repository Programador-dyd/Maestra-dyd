package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Clientes;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveTokenService;
import com.empresa.maestra_dyd_boot.service.ClientesService;
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
public class ClientesArchivadosController {

    private final ClientesService clientesService;
    private final OneDriveTokenService oneDriveTokenService;

    public ClientesArchivadosController(ClientesService clientesService, OneDriveTokenService oneDriveTokenService) {
        this.clientesService = clientesService;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/clientes/archivados")
    public String listarArchivados(
            @RequestParam(required = false) String buscador,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaPrograma,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaSiesa,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        Page<Clientes> resultado = clientesService.buscarArchivadosPaginado(buscador, fechaPrograma, fechaSiesa, estado, pagina);

        model.addAttribute("clientes", resultado.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("totalRegistros", resultado.getTotalElements());

        model.addAttribute("buscador", buscador == null ? "" : buscador);
        model.addAttribute("fechaPrograma", fechaPrograma);
        model.addAttribute("fechaSiesa", fechaSiesa);
        model.addAttribute("estado", estado == null ? "" : estado);

        model.addAttribute("oneDriveConectado", oneDriveTokenService.hayConexionActiva());

        return "clientesArchivados";
    }

    @PostMapping("/clientes/{nit}/restaurar")
    public String restaurar(@PathVariable("nit") String nit) {
        clientesService.restaurar(nit);
        return "redirect:/clientes/archivados";
    }

}