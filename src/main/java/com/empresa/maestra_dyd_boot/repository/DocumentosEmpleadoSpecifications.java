package com.empresa.maestra_dyd_boot.repository;

import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DocumentosEmpleadoSpecifications {

    private DocumentosEmpleadoSpecifications() {
    }

    public static Specification<DocumentosEmpleado> conFiltros(List<String> identificacionesCoincidentes,
                                                                 Integer tipoId,
                                                                 LocalDate fechaDesde,
                                                                 LocalDate fechaHasta) {

        return (root, query, cb) -> {
            var predicados = cb.conjunction();

            if (identificacionesCoincidentes != null) {
                if (identificacionesCoincidentes.isEmpty()) {
                    // Búsqueda de empleado sin resultados: forzamos que no devuelva nada
                    predicados = cb.and(predicados, cb.disjunction());
                } else {
                    predicados = cb.and(predicados, root.get("identificacionEmpleado").in(identificacionesCoincidentes));
                }
            }

            if (tipoId != null) {
                predicados = cb.and(predicados, cb.equal(root.get("tipo").get("id"), tipoId));
            }

            if (fechaDesde != null) {
                predicados = cb.and(predicados, cb.greaterThanOrEqualTo(root.get("fechaSubida"), fechaDesde.atStartOfDay()));
            }

            if (fechaHasta != null) {
                LocalDateTime finDelDia = fechaHasta.atTime(23, 59, 59);
                predicados = cb.and(predicados, cb.lessThanOrEqualTo(root.get("fechaSubida"), finDelDia));
            }

            return predicados;
        };
    }
}