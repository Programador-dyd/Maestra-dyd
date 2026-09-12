package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
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
public class EmpleadosController {

    private final EmpleadosService empleadosService;

    public EmpleadosController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    @GetMapping("/empleados")
    public String listar(
            @RequestParam(required = false) String buscador,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso,
            @RequestParam(defaultValue = "Activo") String estado,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        Page<Empleados> resultado = empleadosService.buscarPaginado(buscador, fechaIngreso, estado, pagina);

        model.addAttribute("empleados", resultado.getContent());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", resultado.getTotalPages());
        model.addAttribute("totalRegistros", resultado.getTotalElements());

        model.addAttribute("buscador", buscador == null ? "" : buscador);
        model.addAttribute("fechaIngreso", fechaIngreso);
        model.addAttribute("estado", estado);

        return "empleados";
    }

    @PostMapping("/empleados/{identificacion}/archivar")
    public String archivar(
            @PathVariable("identificacion") String identificacion,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaRetiro) {
        empleadosService.archivar(identificacion, fechaRetiro);
        return "redirect:/empleados";
    }

    @PostMapping("/empleados/{identificacion}/restaurar")
    public String restaurar(@PathVariable("identificacion") String identificacion) {
        empleadosService.restaurar(identificacion);
        return "redirect:/empleados";
    }

}