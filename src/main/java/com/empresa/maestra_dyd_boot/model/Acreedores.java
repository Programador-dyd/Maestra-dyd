package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "`Acreedores`")
public class Acreedores {

    @Id
    @Column(name = "nit_acreedor", length = 30, nullable = false)
    private String nitAcreedor;

    @Column(name = "razon_social", length = 100)
    private String razonSocial;

    @Column(name = "estado", length = 10)
    private String estado;

    @Column(name = "fecha_programa")
    private LocalDate fechaPrograma;

    @Column(name = "fecha_siesa")
    private LocalDate fechaSiesa;

    @Column(name = "estado_documentacion", length = 20)
    private String estadoDocumentacion;

    @Column(name = "tipo_acreedor", length = 20)
    private String tipoAcreedor;

    public Acreedores() {
    }

    public String getNitAcreedor() {
        return nitAcreedor;
    }

    public void setNitAcreedor(String nitAcreedor) {
        this.nitAcreedor = nitAcreedor;
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

    public String getTipoAcreedor() {
        return tipoAcreedor;
    }

    public void setTipoAcreedor(String tipoAcreedor) {
        this.tipoAcreedor = tipoAcreedor;
    }

    public String getEstadoDocumentacion() {
        return estadoDocumentacion;
    }

    public void setEstadoDocumentacion(String estadoDocumentacion) {
        this.estadoDocumentacion = estadoDocumentacion;
    }
}