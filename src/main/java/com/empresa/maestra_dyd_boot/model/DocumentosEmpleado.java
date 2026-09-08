package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "`DocumentosEmpleado`")
public class DocumentosEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "identificacion_empleado", length = 20)
    private String identificacionEmpleado;

    @Column(name = "`nombreArchivo`", length = 255)
    private String nombreArchivo;

    @Column(name = "`ruta_Documento`", length = 300)
    private String rutaDocumento;

    @Column(name = "onedrive_id", length = 150)
    private String onedriveId;

    @Column(name = "onedrive_url", length = 500)
    private String onedriveUrl;

    @Column(name = "fecha_subida", insertable = false, updatable = false)
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "tipo")
    private TipoDocumentoEmpleado tipo;

    public DocumentosEmpleado() {
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getIdentificacionEmpleado() { return identificacionEmpleado; }
    public void setIdentificacionEmpleado(String identificacionEmpleado) { this.identificacionEmpleado = identificacionEmpleado; }

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

    public TipoDocumentoEmpleado getTipo() { return tipo; }
    public void setTipo(TipoDocumentoEmpleado tipo) { this.tipo = tipo; }
}