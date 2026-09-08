package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveTokenService;
import com.empresa.maestra_dyd_boot.repository.TipoDocumentoEmpleadoRepository;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
import com.empresa.maestra_dyd_boot.service.DocumentosEmpleadoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class DocumentosEmpleadoController {

    private final DocumentosEmpleadoService documentosEmpleadoService;
    private final EmpleadosService empleadosService;
    private final TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository;
    private final OneDriveTokenService oneDriveTokenService;

    public DocumentosEmpleadoController(DocumentosEmpleadoService documentosEmpleadoService,
                                         EmpleadosService empleadosService,
                                         TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository,
                                         OneDriveTokenService oneDriveTokenService) {
        this.documentosEmpleadoService = documentosEmpleadoService;
        this.empleadosService = empleadosService;
        this.tipoDocumentoEmpleadoRepository = tipoDocumentoEmpleadoRepository;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/documentos-empleado/{identificacion}")
    public String verDocumentos(@PathVariable String identificacion, Model model) {
        Empleados empleado = empleadosService.buscarPorIdentificacion(identificacion);

        List<TipoDocumentoEmpleado> tiposDocumento = tipoDocumentoEmpleadoRepository.findAll();

        var tiposPorCategoria = tiposDocumento.stream()
                .collect(Collectors.groupingBy(t -> t.getCategoria().getNombre()));

        model.addAttribute("empleado", empleado);
        model.addAttribute("documentos", documentosEmpleadoService.listarPorEmpleado(identificacion));
        model.addAttribute("tiposPorCategoria", tiposPorCategoria);
        model.addAttribute("tiposPorCategoriaJson", convertirAJson(tiposPorCategoria));

        return "documentosEmpleado";
    }

    @PostMapping("/documentos-empleado/{identificacion}/subir")
    public String subir(@PathVariable String identificacion,
                         @RequestParam Integer tipo,
                         @RequestParam MultipartFile archivo) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=sin_token";
        }

        if (archivo.isEmpty()) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=sin_archivo";
        }

        TipoDocumentoEmpleado tipoDocumento = tipoDocumentoEmpleadoRepository.findById(tipo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no válido"));

        try {
            documentosEmpleadoService.subirDocumento(token.get(), identificacion, tipoDocumento,
                    archivo.getOriginalFilename(), archivo.getBytes());
            return "redirect:/documentos-empleado/" + identificacion + "?ok=1";
        } catch (IOException | RuntimeException e) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=subida";
        }
    }

    @GetMapping("/documentos-empleado/ver/{onedriveId}")
    public void ver(@PathVariable String onedriveId, HttpServletResponse response) throws IOException {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Conexión con OneDrive no disponible");
            return;
        }

        byte[] contenido = documentosEmpleadoService.descargarDocumento(token.get(), onedriveId);
        response.setContentType("application/octet-stream");
        response.getOutputStream().write(contenido);
        response.getOutputStream().flush();
    }

    @PostMapping("/documentos-empleado/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                            @RequestParam String identificacion,
                            @RequestParam(required = false) String onedriveId) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=sin_token";
        }

        documentosEmpleadoService.eliminarDocumento(token.get(), id, onedriveId);
        return "redirect:/documentos-empleado/" + identificacion;
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
            for (TipoDocumentoEmpleado tipo : entrada.getValue()) {
                if (!primerTipo) {
                    json.append(",");
                }
                primerTipo = false;

                json.append("{\"id\":").append(tipo.getId())
                    .append(",\"nombre\":\"").append(escaparJson(tipo.getNombre())).append("\"}");
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