package com.empresa.maestra_dyd_boot.onedrive;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`OneDriveConexion`")
public class OneDriveConexion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "access_token", columnDefinition = "TEXT", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", columnDefinition = "TEXT", nullable = false)
    private String refreshToken;

    @Column(name = "fecha_expiracion_access_token", nullable = false)
    private LocalDateTime fechaExpiracionAccessToken;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public OneDriveConexion() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public LocalDateTime getFechaExpiracionAccessToken() {
        return fechaExpiracionAccessToken;
    }

    public void setFechaExpiracionAccessToken(LocalDateTime fechaExpiracionAccessToken) {
        this.fechaExpiracionAccessToken = fechaExpiracionAccessToken;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean estaPorExpirar() {
        // Consideramos que "está por expirar" si faltan menos de 10 minutos
        return fechaExpiracionAccessToken.isBefore(LocalDateTime.now().plusMinutes(10));
    }
}