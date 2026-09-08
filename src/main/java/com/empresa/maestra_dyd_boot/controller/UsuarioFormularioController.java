package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Persona;
import com.empresa.maestra_dyd_boot.repository.PersonaRepository;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
import com.empresa.maestra_dyd_boot.service.PersonaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class UsuarioFormularioController {

    private final PersonaService personaService;
    private final PersonaRepository personaRepository;
    private final EmpleadosService empleadosService;

    public UsuarioFormularioController(PersonaService personaService, PersonaRepository personaRepository,
                                        EmpleadosService empleadosService) {
        this.personaService = personaService;
        this.personaRepository = personaRepository;
        this.empleadosService = empleadosService;
    }

    @GetMapping("/usuarioFormulario")
    public String mostrarFormulario(@RequestParam(required = false) String identificacion, Model model) {

        Persona usuario;
        boolean esModificar = identificacion != null && !identificacion.isBlank();

        if (esModificar) {
            usuario = personaRepository.findById(identificacion)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + identificacion));
            usuario.setClave(""); // Nunca precargamos el hash de la clave en el formulario
        } else {
            usuario = new Persona();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("esModificar", esModificar);
        model.addAttribute("identificacionAnterior", esModificar ? identificacion : "");

        return "usuarioFormulario";
    }

    @PostMapping("/usuarioFormulario")
    public String guardar(@ModelAttribute Persona usuario, @RequestParam String identificacionAnterior,
                           @RequestParam(required = false) LocalDate fechaIngresoEmpleado) {

        if (identificacionAnterior != null && !identificacionAnterior.isBlank()) {
            personaService.modificar(identificacionAnterior, usuario);
        } else {
            personaService.grabar(usuario);
        }

        if ("E".equals(usuario.getRol())) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            empleadosService.crearOVincularDesdeUsuario(usuario.getIdentificacion(), nombreCompleto, fechaIngresoEmpleado);
        }

        return "redirect:/usuarios";
    }

}