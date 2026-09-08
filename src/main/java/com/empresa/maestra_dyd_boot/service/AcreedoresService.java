package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Acreedores;
import com.empresa.maestra_dyd_boot.repository.AcreedoresRepository;
import com.empresa.maestra_dyd_boot.repository.AcreedoresSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AcreedoresService {

    private static final int REGISTROS_POR_PAGINA = 30;

    private final AcreedoresRepository acreedoresRepository;

    public AcreedoresService(AcreedoresRepository acreedoresRepository) {
        this.acreedoresRepository = acreedoresRepository;
    }

    public Page<Acreedores> buscarPaginado(String buscador, LocalDate fechaPrograma, LocalDate fechaSiesa,
                                            String estadoDocumentacion, String estado, int pagina) {
        int paginaIndex = Math.max(pagina - 1, 0);
        var pageable = PageRequest.of(paginaIndex, REGISTROS_POR_PAGINA, Sort.by("razonSocial").ascending());
        var specification = AcreedoresSpecifications.conFiltros(buscador, fechaPrograma, fechaSiesa, estadoDocumentacion, estado);
        return acreedoresRepository.findAll(specification, pageable);
    }

    public Acreedores buscarPorNit(String nitAcreedor) {
        return acreedoresRepository.findById(nitAcreedor)
                .orElseThrow(() -> new IllegalArgumentException("Acreedor no encontrado: " + nitAcreedor));
    }

    public Acreedores guardar(Acreedores acreedor) {
        return acreedoresRepository.save(acreedor);
    }

    public Acreedores actualizar(String nitAnterior, Acreedores datosNuevos) {
        if (nitAnterior != null && !nitAnterior.equals(datosNuevos.getNitAcreedor())) {
            acreedoresRepository.deleteById(nitAnterior);
        }
        return acreedoresRepository.save(datosNuevos);
    }

    public void archivar(String nitAcreedor) {
        Acreedores acreedor = buscarPorNit(nitAcreedor);
        acreedor.setEstado("Inactivo");
        acreedoresRepository.save(acreedor);
    }

    public void restaurar(String nitAcreedor) {
        Acreedores acreedor = buscarPorNit(nitAcreedor);
        acreedor.setEstado("Activo");
        acreedoresRepository.save(acreedor);
    }
}