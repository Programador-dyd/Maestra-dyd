package com.empresa.maestra_dyd_boot.config;

import com.empresa.maestra_dyd_boot.model.Persona;
import com.empresa.maestra_dyd_boot.repository.PersonaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PersonaUserDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepository;

    public PersonaUserDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identificacion) throws UsernameNotFoundException {
        Persona persona = personaRepository.findById(identificacion)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identificacion));

        return User.builder()
                .username(persona.getIdentificacion())
                .password(persona.getClave())
                .authorities(new SimpleGrantedAuthority("ROLE_" + persona.getRol().trim().toUpperCase()))
                .build();
    }
}