package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProveedoresRepository extends JpaRepository<Proveedores, String>, JpaSpecificationExecutor<Proveedores> {

}