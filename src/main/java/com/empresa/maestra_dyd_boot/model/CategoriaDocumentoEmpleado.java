package com.empresa.maestra_dyd_boot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "`CategoriaDocumentoEmpleado`")
public class CategoriaDocumentoEmpleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    public CategoriaDocumentoEmpleado() {
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
}