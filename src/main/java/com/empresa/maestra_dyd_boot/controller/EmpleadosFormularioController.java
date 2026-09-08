package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmpleadosFormularioController {

    private final EmpleadosService empleadosService;

    public EmpleadosFormularioController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    @GetMapping("/empleadosFormulario")
    public String mostrarFormulario(@RequestParam(required = false) String identificacion, Model model) {

        Empleados empleado;
        boolean esModificar = identificacion != null && !identificacion.isBlank();

        if (esModificar) {
            empleado = empleadosService.buscarPorIdentificacion(identificacion);
        } else {
            empleado = new Empleados();
            empleado.setEstado("Activo");
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("esModificar", esModificar);
        model.addAttribute("identificacionAnterior", esModificar ? identificacion : "");

        return "empleadosFormulario";
    }

    @PostMapping("/empleadosFormulario")
    public String guardar(@ModelAttribute Empleados empleado, @RequestParam String identificacionAnterior) {

        if (identificacionAnterior != null && !identificacionAnterior.isBlank()) {
            empleadosService.actualizar(identificacionAnterior, empleado);
        } else {
            empleadosService.guardar(empleado);
        }

        return "redirect:/empleados";
    }

}