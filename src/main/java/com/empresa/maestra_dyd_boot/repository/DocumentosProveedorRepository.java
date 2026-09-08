package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.DocumentosProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentosProveedorRepository extends JpaRepository<DocumentosProveedor, Integer> {

    List<DocumentosProveedor> findByNitProveedorOrderByFechaSubidaDesc(String nitProveedor);

    boolean existsByNombreArchivo(String nombreArchivo);

}