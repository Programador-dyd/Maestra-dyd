package com.empresa.maestra_dyd_boot.web;

import com.empresa.maestra_dyd_boot.repository.PersonaRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    private final PersonaRepository personaRepository;

    public GlobalModelAttributes(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @ModelAttribute
    public void agregarUsuarioActual(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        personaRepository.findById(authentication.getName())
                .ifPresent(persona -> model.addAttribute("usuarioActual", persona));
    }

    @ModelAttribute("estado")
    public String estadoPorDefecto() {
        return "";
    }

}