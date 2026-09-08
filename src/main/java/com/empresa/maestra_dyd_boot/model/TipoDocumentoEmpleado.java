package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "`TipoDocumentoEmpleado`")
public class TipoDocumentoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaDocumentoEmpleado categoria;

    public TipoDocumentoEmpleado() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public CategoriaDocumentoEmpleado getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaDocumentoEmpleado categoria) {
        this.categoria = categoria;
    }
}