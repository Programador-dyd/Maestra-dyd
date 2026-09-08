package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoDocumentoEmpleadoRepository extends JpaRepository<TipoDocumentoEmpleado, Integer> {

    List<TipoDocumentoEmpleado> findByCategoriaIdOrderByNombre(Integer categoriaId);

}