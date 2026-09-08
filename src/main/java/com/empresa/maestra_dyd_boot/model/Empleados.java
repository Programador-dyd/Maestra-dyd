package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "`Empleados`")
public class Empleados {

    @Id
    @Column(name = "identificacion", length = 20, nullable = false)
    private String identificacion;

    @Column(name = "nombre_empleado", length = 150)
    private String nombreEmpleado;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fecha_retiro")
    private LocalDate fechaRetiro;

    public Empleados() {
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDate getFechaRetiro() {
        return fechaRetiro;
    }

    public void setFechaRetiro(LocalDate fechaRetiro) {
        this.fechaRetiro = fechaRetiro;
    }
}