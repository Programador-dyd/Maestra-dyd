package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Acreedores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AcreedoresRepository extends JpaRepository<Acreedores, String>, JpaSpecificationExecutor<Acreedores> {

}