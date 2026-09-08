package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "`Persona`")
public class Persona {

    @Id
    @Column(name = "identificacion", length = 50, nullable = false)
    private String identificacion;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "apellido", length = 50)
    private String apellido;

    @Column(name = "clave", length = 150)
    private String clave;

    @Column(name = "rol", length = 15, nullable = false)
    private String rol;

    // Constructor vacío requerido por JPA
    public Persona() {
    }

    // Getters y setters
    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoEnObjeto() {
    if (rol == null) {
        return "Desconocido";
    }
    return switch (rol.trim()) {
        case "A" -> "Administrador";
        case "U" -> "Usuario";
        case "E" -> "Empleado";
        default -> "Desconocido";
    };
}
}