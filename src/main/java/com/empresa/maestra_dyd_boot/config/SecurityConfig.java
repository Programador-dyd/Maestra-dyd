package com.empresa.maestra_dyd_boot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final PersonaAuthenticationSuccessHandler successHandler;

    public SecurityConfig(PersonaAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/img/**", "/logo/**", "/estilos/**", "/iconos/**", "/icons/**").permitAll()
                .requestMatchers("/documentos-empleado/**").hasAnyRole("A", "E")
                .requestMatchers("/usuarios", "/usuarios/**", "/usuarioFormulario", "/tipos-documento/**",
                                "/acreedores/**", "/acreedoresFormulario", "/documentos-acreedor/**",
                                "/proveedores/**", "/proveedoresFormulario", "/documentos-proveedor/**",
                                "/empleados/**", "/empleadosFormulario").hasRole("A")
                .requestMatchers("/inicio", "/clientes/**", "/clientesFormulario", "/documentos/**",
                                "/onedrive/**", "/oauth/**").hasAnyRole("A", "U")
                .anyRequest().hasRole("A")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("identificacion")
                .passwordParameter("clave")
                .successHandler(successHandler)
                .failureUrl("/login?error=1")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=1")
                .permitAll()
            )
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/login?error=sin_permiso")
            );

        return http.build();
    }

}