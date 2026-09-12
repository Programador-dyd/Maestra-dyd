package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.repository.EmpleadosRepository;
import com.empresa.maestra_dyd_boot.repository.EmpleadosSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpleadosService {

    private static final int REGISTROS_POR_PAGINA = 30;

    private final EmpleadosRepository empleadosRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository) {
        this.empleadosRepository = empleadosRepository;
    }

    public Page<Empleados> buscarPaginado(String buscador, LocalDate fechaIngreso, String estado, int pagina) {
        int paginaIndex = Math.max(pagina - 1, 0);
        var pageable = PageRequest.of(paginaIndex, REGISTROS_POR_PAGINA, Sort.by("nombreEmpleado").ascending());
        var specification = EmpleadosSpecifications.conFiltros(buscador, fechaIngreso, estado);
        return empleadosRepository.findAll(specification, pageable);
    }

    public Empleados buscarPorIdentificacion(String identificacion) {
        return empleadosRepository.findById(identificacion)
                .orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado: " + identificacion));
    }

    public List<String> buscarCoincidencias(String buscador) {
        String buscadorMin = buscador.toLowerCase();
        return empleadosRepository.findAll().stream()
                .filter(e -> e.getIdentificacion().toLowerCase().contains(buscadorMin)
                        || (e.getNombreEmpleado() != null && e.getNombreEmpleado().toLowerCase().contains(buscadorMin)))
                .map(Empleados::getIdentificacion)
                .toList();
    }

    public Empleados guardar(Empleados empleado) {
        return empleadosRepository.save(empleado);
    }

    public Empleados actualizar(String identificacionAnterior, Empleados datosNuevos) {
        if (identificacionAnterior != null && !identificacionAnterior.equals(datosNuevos.getIdentificacion())) {
            empleadosRepository.deleteById(identificacionAnterior);
        }
        return empleadosRepository.save(datosNuevos);
    }

    public void archivar(String identificacion, LocalDate fechaRetiro) {
        Empleados empleado = buscarPorIdentificacion(identificacion);
        empleado.setEstado("Inactivo");
        empleado.setFechaRetiro(fechaRetiro != null ? fechaRetiro : LocalDate.now());
        empleadosRepository.save(empleado);
    }

    public void restaurar(String identificacion) {
        Empleados empleado = buscarPorIdentificacion(identificacion);
        empleado.setEstado("Activo");
        empleado.setFechaRetiro(null);
        empleadosRepository.save(empleado);
    }

    public void crearOVincularDesdeUsuario(String identificacion, String nombreCompleto, LocalDate fechaIngreso) {
        empleadosRepository.findById(identificacion).ifPresentOrElse(
            empleadoExistente -> {
                // Ya existe (por ejemplo, importado del Excel) — no se sobrescribe, solo queda vinculado por la misma identificación.
            },
            () -> {
                Empleados nuevo = new Empleados();
                nuevo.setIdentificacion(identificacion);
                nuevo.setNombreEmpleado(nombreCompleto);
                nuevo.setFechaIngreso(fechaIngreso);
                nuevo.setEstado("Activo");
                empleadosRepository.save(nuevo);
            }
        );
    }
}