package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentosEmpleadoRepository extends JpaRepository<DocumentosEmpleado, Integer> {

    List<DocumentosEmpleado> findByIdentificacionEmpleadoOrderByFechaSubidaDesc(String identificacionEmpleado);

    boolean existsByNombreArchivo(String nombreArchivo);

}