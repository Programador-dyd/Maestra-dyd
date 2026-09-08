package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Documentos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentosRepository extends JpaRepository<Documentos, Integer> {

    List<Documentos> findByNitClienteOrderByFechaSubidaDesc(String nitCliente);

    boolean existsByNombreArchivo(String nombreArchivo);

}