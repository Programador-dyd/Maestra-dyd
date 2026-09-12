package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Clientes;
import com.empresa.maestra_dyd_boot.repository.ClientesRepository;
import com.empresa.maestra_dyd_boot.repository.ClientesSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ClientesService {

    private static final int REGISTROS_POR_PAGINA = 30;

    private final ClientesRepository clientesRepository;

    public ClientesService(ClientesRepository clientesRepository) {
        this.clientesRepository = clientesRepository;
    }

    public Page<Clientes> buscarPaginado(String buscador, LocalDate fechaPrograma, LocalDate fechaSiesa,
                                          String estadoDocumentacion, String estado, int pagina) {

        int paginaIndex = Math.max(pagina - 1, 0); // Spring pagina desde 0, el usuario ve desde 1

        var pageable = PageRequest.of(paginaIndex, REGISTROS_POR_PAGINA, Sort.by("razonSocial").ascending());

        var specification = ClientesSpecifications.conFiltros(estado, buscador, fechaPrograma, fechaSiesa, estadoDocumentacion);

        return clientesRepository.findAll(specification, pageable);
    }

    public Clientes buscarPorNit(String nitCliente) {
        return clientesRepository.findById(nitCliente)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + nitCliente));
    }

    public Clientes guardar(Clientes cliente) {
        return clientesRepository.save(cliente);
    }

    public Clientes actualizar(String nitAnterior, Clientes datosNuevos) {
        if (nitAnterior != null && !nitAnterior.equals(datosNuevos.getNitCliente())) {
            // El NIT cambió: hay que mover el registro (borrar el viejo, crear el nuevo)
            clientesRepository.deleteById(nitAnterior);
        }
        return clientesRepository.save(datosNuevos);
    }

    public void archivar(String nitCliente) {
        Clientes cliente = buscarPorNit(nitCliente);
        cliente.setEstado("Inactivo");
        clientesRepository.save(cliente);
    }

    public void restaurar(String nitCliente) {
        Clientes cliente = buscarPorNit(nitCliente);
        cliente.setEstado("Activo");
        clientesRepository.save(cliente);
    }
}