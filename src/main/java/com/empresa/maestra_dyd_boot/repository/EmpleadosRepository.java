package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmpleadosRepository extends JpaRepository<Empleados, String>, JpaSpecificationExecutor<Empleados> {

}