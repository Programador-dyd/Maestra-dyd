package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import com.empresa.maestra_dyd_boot.repository.TipoDocumentoEmpleadoRepository;
import com.empresa.maestra_dyd_boot.service.DocumentosEmpleadoService;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DocumentosEmpleadoBusquedaController {

    private final DocumentosEmpleadoService documentosEmpleadoService;
    private final EmpleadosService empleadosService;
    private final TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository;

    public DocumentosEmpleadoBusquedaController(DocumentosEmpleadoService documentosEmpleadoService,
                                                 EmpleadosService empleadosService,
                                                 TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository) {
        this.documentosEmpleadoService = documentosEmpleadoService;
        this.empleadosService = empleadosService;
        this.tipoDocumentoEmpleadoRepository = tipoDocumentoEmpleadoRepository;
    }

    @GetMapping("/documentos-empleado-busqueda")
    public String buscar(
            @RequestParam(required = false) String buscador,
            @RequestParam(required = false) Integer tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            Model model) {

        List<DocumentosEmpleado> documentos =
                documentosEmpleadoService.buscarGlobal(buscador, tipo, fechaDesde, fechaHasta);

        // Armamos un mapa identificación -> nombre del empleado, para mostrar en la tabla sin N+1 consultas
        Map<String, String> nombresPorIdentificacion = new HashMap<>();
        for (DocumentosEmpleado doc : documentos) {
            nombresPorIdentificacion.computeIfAbsent(doc.getIdentificacionEmpleado(), id -> {
                try {
                    Empleados e = empleadosService.buscarPorIdentificacion(id);
                    return e.getNombreEmpleado();
                } catch (Exception ex) {
                    return "(desconocido)";
                }
            });
        }

        List<TipoDocumentoEmpleado> tiposDocumento = tipoDocumentoEmpleadoRepository.findAll();
        var tiposPorCategoria = tiposDocumento.stream()
                .collect(Collectors.groupingBy(t -> t.getCategoria().getNombre()));

        model.addAttribute("documentos", documentos);
        model.addAttribute("nombresPorIdentificacion", nombresPorIdentificacion);
        model.addAttribute("tiposPorCategoria", tiposPorCategoria);
        model.addAttribute("tiposPorCategoriaJson", convertirAJson(tiposPorCategoria));
        model.addAttribute("buscador", buscador == null ? "" : buscador);
        model.addAttribute("tipo", tipo);
        model.addAttribute("fechaDesde", fechaDesde);
        model.addAttribute("fechaHasta", fechaHasta);

        return "documentosEmpleadoBusqueda";
    }

    private String convertirAJson(Map<String, List<TipoDocumentoEmpleado>> tiposPorCategoria) {
        StringBuilder json = new StringBuilder("{");
        boolean primeraCategoria = true;

        for (var entrada : tiposPorCategoria.entrySet()) {
            if (!primeraCategoria) {
                json.append(",");
            }
            primeraCategoria = false;

            json.append("\"").append(escaparJson(entrada.getKey())).append("\":[");

            boolean primerTipo = true;
            for (TipoDocumentoEmpleado tipoDoc : entrada.getValue()) {
                if (!primerTipo) {
                    json.append(",");
                }
                primerTipo = false;

                json.append("{\"id\":").append(tipoDoc.getId())
                    .append(",\"nombre\":\"").append(escaparJson(tipoDoc.getNombre())).append("\"}");
            }

            json.append("]");
        }

        json.append("}");
        return json.toString();
    }

    private String escaparJson(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}