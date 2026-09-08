package com.empresa.maestra_dyd_boot.config;

import com.empresa.maestra_dyd_boot.repository.PersonaRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PersonaAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonaAuthenticationSuccessHandler(PersonaRepository personaRepository, PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
        setDefaultTargetUrl("/inicio");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        String identificacion = authentication.getName();
        String claveIngresada = request.getParameter("clave");

        personaRepository.findById(identificacion).ifPresent(persona -> {
            String claveGuardada = persona.getClave();
            boolean esBCrypt = claveGuardada != null &&
                    (claveGuardada.startsWith("$2a$") || claveGuardada.startsWith("$2b$") || claveGuardada.startsWith("$2y$"));

            if (!esBCrypt && claveIngresada != null) {
                persona.setClave(passwordEncoder.encode(claveIngresada));
                personaRepository.save(persona);
            }
        });

        super.onAuthenticationSuccess(request, response, authentication);
    }
}