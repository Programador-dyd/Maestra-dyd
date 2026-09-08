package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaRepository extends JpaRepository<Persona, String> {

    List<Persona> findByRolNot(String rol);

}