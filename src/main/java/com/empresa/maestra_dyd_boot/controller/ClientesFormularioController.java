package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Clientes;
import com.empresa.maestra_dyd_boot.service.ClientesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@Controller
public class ClientesFormularioController {

    private final ClientesService clientesService;

    public ClientesFormularioController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    @GetMapping("/clientesFormulario")
    public String mostrarFormulario(@RequestParam(required = false) String nitCliente, Model model) {

        Clientes cliente;
        boolean esModificar = nitCliente != null && !nitCliente.isBlank();

        if (esModificar) {
            cliente = clientesService.buscarPorNit(nitCliente);
        } else {
            cliente = new Clientes();
            cliente.setEstado("Activo");
            cliente.setEstadoDocumentacion("INCOMPLETO");
        }

        model.addAttribute("cliente", cliente);
        model.addAttribute("esModificar", esModificar);
        model.addAttribute("nitAnterior", esModificar ? nitCliente : "");

        return "clientesFormulario";
    }

    @PostMapping("/clientesFormulario")
    public String guardar(@ModelAttribute Clientes cliente, @RequestParam String nitAnterior) {

        if (nitAnterior != null && !nitAnterior.isBlank()) {
            clientesService.actualizar(nitAnterior, cliente);
        } else {
            clientesService.guardar(cliente);
        }

        return "redirect:/clientes";
    }

}