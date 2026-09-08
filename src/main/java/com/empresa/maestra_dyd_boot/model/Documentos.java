package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`Documentos`")
public class Documentos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nit_cliente", length = 30)
    private String nitCliente;

    @Column(name = "`nombreArchivo`", length = 255)
    private String nombreArchivo;

    @Column(name = "ruta_Documento", length = 300)
    private String rutaDocumento;

    @Column(name = "onedrive_id", length = 150)
    private String onedriveId;

    @Column(name = "onedrive_url", length = 500)
    private String onedriveUrl;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @Column(name = "tipo")
    private Long tipo;

    public Documentos() {
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNitCliente() { return nitCliente; }
    public void setNitCliente(String nitCliente) { this.nitCliente = nitCliente; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getRutaDocumento() { return rutaDocumento; }
    public void setRutaDocumento(String rutaDocumento) { this.rutaDocumento = rutaDocumento; }

    public String getOnedriveId() { return onedriveId; }
    public void setOnedriveId(String onedriveId) { this.onedriveId = onedriveId; }

    public String getOnedriveUrl() { return onedriveUrl; }
    public void setOnedriveUrl(String onedriveUrl) { this.onedriveUrl = onedriveUrl; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public Long getTipo() { return tipo; }
    public void setTipo(Long tipo) { this.tipo = tipo; }
}