package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.DocumentosAcreedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentosAcreedorRepository extends JpaRepository<DocumentosAcreedor, Integer> {

    List<DocumentosAcreedor> findByNitAcreedorOrderByFechaSubidaDesc(String nitAcreedor);

    boolean existsByNombreArchivo(String nombreArchivo);

}