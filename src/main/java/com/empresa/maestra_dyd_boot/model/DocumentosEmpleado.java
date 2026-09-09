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

    @Column(name = "s3_key", length = 500)
    private String s3Key;

    @Column(name = "s3_url", length = 500)
    private String s3Url;

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

    public String getS3Key() { return s3Key; }
    public void setS3Key(String s3Key) { this.s3Key = s3Key; }

    public String getS3Url() { return s3Url; }
    public void setS3Url(String s3Url) { this.s3Url = s3Url; }

    public LocalDateTime getFechaSubida() { return fechaSubida; }
    public void setFechaSubida(LocalDateTime fechaSubida) { this.fechaSubida = fechaSubida; }

    public TipoDocumentoEmpleado getTipo() { return tipo; }
    public void setTipo(TipoDocumentoEmpleado tipo) { this.tipo = tipo; }
}