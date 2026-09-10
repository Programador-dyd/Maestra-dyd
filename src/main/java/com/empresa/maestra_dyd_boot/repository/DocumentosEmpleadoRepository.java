package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DocumentosEmpleadoRepository extends JpaRepository<DocumentosEmpleado, Integer>, JpaSpecificationExecutor<DocumentosEmpleado> {

    List<DocumentosEmpleado> findByIdentificacionEmpleadoOrderByFechaSubidaDesc(String identificacionEmpleado);

    boolean existsByNombreArchivo(String nombreArchivo);

    Optional<DocumentosEmpleado> findByIdentificacionEmpleadoAndTipoId(String identificacionEmpleado, Integer tipoId);

}