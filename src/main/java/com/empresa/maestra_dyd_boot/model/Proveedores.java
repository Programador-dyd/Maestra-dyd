package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "`Proveedores`")
public class Proveedores {

    @Id
    @Column(name = "nit_proveedor", length = 30, nullable = false)
    private String nitProveedor;

    @Column(name = "razon_social", length = 100)
    private String razonSocial;

    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "estado_documentacion", length = 20)
    private String estadoDocumentacion;

    @Column(name = "fecha_programa")
    private LocalDate fechaPrograma;

    @Column(name = "fecha_siesa")
    private LocalDate fechaSiesa;

    @Column(name = "tipo_proveedor", length = 20)
    private String tipoProveedor;

    public Proveedores() {
    }

    public String getNitProveedor() {
        return nitProveedor;
    }

    public void setNitProveedor(String nitProveedor) {
        this.nitProveedor = nitProveedor;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstadoDocumentacion() {
        return estadoDocumentacion;
    }

    public void setEstadoDocumentacion(String estadoDocumentacion) {
        this.estadoDocumentacion = estadoDocumentacion;
    }

    public LocalDate getFechaPrograma() {
        return fechaPrograma;
    }

    public void setFechaPrograma(LocalDate fechaPrograma) {
        this.fechaPrograma = fechaPrograma;
    }

    public LocalDate getFechaSiesa() {
        return fechaSiesa;
    }

    public void setFechaSiesa(LocalDate fechaSiesa) {
        this.fechaSiesa = fechaSiesa;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }
}