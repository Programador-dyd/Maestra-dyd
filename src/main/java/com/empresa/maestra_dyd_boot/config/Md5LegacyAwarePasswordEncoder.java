package com.empresa.maestra_dyd_boot.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5LegacyAwarePasswordEncoder implements PasswordEncoder {

    private final BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        // Toda contraseña NUEVA (registro, cambio de clave) siempre se guarda en BCrypt
        return bcryptEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null) {
            return false;
        }

        if (esHashBCrypt(encodedPassword)) {
            return bcryptEncoder.matches(rawPassword, encodedPassword);
        }

        String md5Calculado = calcularMD5(rawPassword.toString());
        return md5Calculado != null && md5Calculado.equalsIgnoreCase(encodedPassword);
    }

    private boolean esHashBCrypt(String valor) {
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
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