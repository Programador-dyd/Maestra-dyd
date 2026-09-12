package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.Empleados;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class EmpleadosSpecifications {

    private EmpleadosSpecifications() {
    }

    public static Specification<Empleados> conFiltros(String buscador, LocalDate fechaIngreso, String estado) {

        return (root, query, cb) -> {
            var predicados = cb.equal(cb.literal(1), 1);

            if (buscador != null && !buscador.isBlank()) {
                String patron = "%" + buscador.toLowerCase() + "%";
                var porIdentificacion = cb.like(cb.lower(root.get("identificacion")), patron);
                var porNombre = cb.like(cb.lower(root.get("nombreEmpleado")), patron);
                predicados = cb.and(predicados, cb.or(porIdentificacion, porNombre));
            }

            if (fechaIngreso != null) {
                predicados = cb.and(predicados, cb.equal(root.get("fechaIngreso"), fechaIngreso));
            }

            if (estado != null && !estado.isBlank() && !estado.equals("TODOS")) {
                predicados = cb.and(predicados, cb.equal(root.get("estado"), estado));
            }

            return predicados;
        };
    }
}