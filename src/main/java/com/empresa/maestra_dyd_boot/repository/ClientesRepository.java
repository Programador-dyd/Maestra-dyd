package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientesRepository extends JpaRepository<Clientes, String>, JpaSpecificationExecutor<Clientes> {

}