package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Persona;
import com.empresa.maestra_dyd_boot.repository.PersonaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonaService(PersonaRepository personaRepository, PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retorna ptional.empty() si las credenciales son incorrectas.
     * Retorna la Persona si las credenciales son correcta. 
     */
    public Optional<Persona> validar(String identificacion, String claveIngresada) {
        Optional<Persona> personaOpt = personaRepository.findById(identificacion);

        if (personaOpt.isEmpty()) {
            return Optional.empty();
        }

        Persona persona = personaOpt.get();
        String claveGuardada = persona.getClave();

        if (esHashBCrypt(claveGuardada)) {
            // Caso 1: ya migrado a BCrypt
            if (passwordEncoder.matches(claveIngresada, claveGuardada)) {
                return Optional.of(persona);
            }
        } else {
            // Caso 2: todavía en MD5 (formato viejo)
            String md5Ingresada = calcularMD5(claveIngresada);
            if (md5Ingresada != null && md5Ingresada.equalsIgnoreCase(claveGuardada)) {
                // Login correcto con clave vieja: migramos a BCrypt 
                persona.setClave(passwordEncoder.encode(claveIngresada));
                personaRepository.save(persona);
                return Optional.of(persona);
            }
        }

        return Optional.empty();
    }


    /**
     * Reemplaza al antiguo .grabar().
     * Crea una nueva persona, hasheando la clave con BCrypt.
     */
    public Persona grabar(Persona persona) {
        persona.setClave(passwordEncoder.encode(persona.getClave()));
        return personaRepository.save(persona);
    }

    /**
     * Actualiza los datos de una persona existente.
     * Si se envía una nueva clave, se re-hashea con BCrypt; si no, se conserva la anterior.
     */
    public Persona modificar(String identificacionAnterior, Persona datosNuevos) {
        Persona personaExistente = personaRepository.findById(identificacionAnterior)
                .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada: " + identificacionAnterior));

        personaExistente.setIdentificacion(datosNuevos.getIdentificacion());
        personaExistente.setNombre(datosNuevos.getNombre());
        personaExistente.setApellido(datosNuevos.getApellido());
        personaExistente.setRol(datosNuevos.getRol());

        if (datosNuevos.getClave() != null && !datosNuevos.getClave().isBlank()) {
            personaExistente.setClave(passwordEncoder.encode(datosNuevos.getClave()));
        }

        return personaRepository.save(personaExistente);
    }

    public List<Persona> listarUsuarios() {
        return personaRepository.findByRolNot("C");
    }

    public void eliminar(String identificacion) {
        personaRepository.deleteById(identificacion);
    }

    private boolean esHashBCrypt(String valor) {
        return valor != null && (valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$"));
    }

    private String calcularMD5(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(texto.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}