package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Clientes;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ClientesSpecifications {

    private ClientesSpecifications() {
        // Clase de utilidades, no se instancia
    }

    public static Specification<Clientes> conFiltros(String estado, String buscador, LocalDate fechaPrograma,
                                                       LocalDate fechaSiesa, String estadoDocumentacion) {

        return (root, query, cb) -> {
            var predicados = cb.equal(cb.literal(1), 1);

            if (estado != null && !estado.isBlank() && !estado.equals("TODOS")) {
                predicados = cb.and(predicados, cb.equal(root.get("estado"), estado));
            }

            if (buscador != null && !buscador.isBlank()) {
                String patron = "%" + buscador.toLowerCase() + "%";
                var porNit = cb.like(cb.lower(root.get("nitCliente")), patron);
                var porRazonSocial = cb.like(cb.lower(root.get("razonSocial")), patron);
                predicados = cb.and(predicados, cb.or(porNit, porRazonSocial));
            }

            if (fechaPrograma != null) {
                predicados = cb.and(predicados, cb.equal(root.get("fechaPrograma"), fechaPrograma));
            }

            if (fechaSiesa != null) {
                predicados = cb.and(predicados, cb.equal(root.get("fechaSiesa"), fechaSiesa));
            }

            if (estadoDocumentacion != null && !estadoDocumentacion.isBlank()) {
                predicados = cb.and(predicados, cb.equal(root.get("estadoDocumentacion"), estadoDocumentacion));
            }

            return predicados;
        };
    }
}