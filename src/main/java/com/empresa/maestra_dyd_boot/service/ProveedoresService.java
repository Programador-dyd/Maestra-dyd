package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import com.empresa.maestra_dyd_boot.repository.ProveedoresRepository;
import com.empresa.maestra_dyd_boot.repository.ProveedoresSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProveedoresService {

    private static final int REGISTROS_POR_PAGINA = 30;

    private final ProveedoresRepository proveedoresRepository;

    public ProveedoresService(ProveedoresRepository proveedoresRepository) {
        this.proveedoresRepository = proveedoresRepository;
    }

    public Page<Proveedores> buscarPaginado(String buscador, LocalDate fechaPrograma, LocalDate fechaSiesa,
                                             String estadoDocumentacion, String estado, int pagina) {
        int paginaIndex = Math.max(pagina - 1, 0);
        var pageable = PageRequest.of(paginaIndex, REGISTROS_POR_PAGINA, Sort.by("razonSocial").ascending());
        var specification = ProveedoresSpecifications.conFiltros(buscador, fechaPrograma, fechaSiesa, estadoDocumentacion, estado);
        return proveedoresRepository.findAll(specification, pageable);
    }

    public Proveedores buscarPorNit(String nitProveedor) {
        return proveedoresRepository.findById(nitProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado: " + nitProveedor));
    }

    public Proveedores guardar(Proveedores proveedor) {
        return proveedoresRepository.save(proveedor);
    }

    public Proveedores actualizar(String nitAnterior, Proveedores datosNuevos) {
        if (nitAnterior != null && !nitAnterior.equals(datosNuevos.getNitProveedor())) {
            proveedoresRepository.deleteById(nitAnterior);
        }
        return proveedoresRepository.save(datosNuevos);
    }

    public void archivar(String nitProveedor) {
        Proveedores proveedor = buscarPorNit(nitProveedor);
        proveedor.setEstado("Inactivo");
        proveedoresRepository.save(proveedor);
    }

    public void restaurar(String nitProveedor) {
        Proveedores proveedor = buscarPorNit(nitProveedor);
        proveedor.setEstado("Activo");
        proveedoresRepository.save(proveedor);
    }
}