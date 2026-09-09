package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import com.empresa.maestra_dyd_boot.repository.TipoDocumentoEmpleadoRepository;
import com.empresa.maestra_dyd_boot.service.EmpleadosService;
import com.empresa.maestra_dyd_boot.service.DocumentosEmpleadoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DocumentosEmpleadoController {

    private final DocumentosEmpleadoService documentosEmpleadoService;
    private final EmpleadosService empleadosService;
    private final TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository;

    public DocumentosEmpleadoController(DocumentosEmpleadoService documentosEmpleadoService,
                                         EmpleadosService empleadosService,
                                         TipoDocumentoEmpleadoRepository tipoDocumentoEmpleadoRepository) {
        this.documentosEmpleadoService = documentosEmpleadoService;
        this.empleadosService = empleadosService;
        this.tipoDocumentoEmpleadoRepository = tipoDocumentoEmpleadoRepository;
    }

    @GetMapping("/documentos-empleado/{identificacion}")
    public String verDocumentos(@PathVariable String identificacion, Model model,
                                 Authentication authentication) {
        verificarAcceso(identificacion, authentication);

        Empleados empleado = empleadosService.buscarPorIdentificacion(identificacion);

        List<TipoDocumentoEmpleado> tiposDocumento = tipoDocumentoEmpleadoRepository.findAll();

        var tiposPorCategoria = tiposDocumento.stream()
                .collect(Collectors.groupingBy(t -> t.getCategoria().getNombre()));

        model.addAttribute("empleado", empleado);
        model.addAttribute("documentos", documentosEmpleadoService.listarPorEmpleado(identificacion));
        model.addAttribute("tiposPorCategoria", tiposPorCategoria);
        model.addAttribute("tiposPorCategoriaJson", convertirAJson(tiposPorCategoria));
        model.addAttribute("s3Disponible", documentosEmpleadoService.s3Disponible());

        return "documentosEmpleado";
    }

    @PostMapping("/documentos-empleado/{identificacion}/subir")
    public String subir(@PathVariable String identificacion,
                         @RequestParam Integer tipo,
                         @RequestParam MultipartFile archivo,
                         Authentication authentication) {
        verificarAcceso(identificacion, authentication);

        if (!documentosEmpleadoService.s3Disponible()) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=sin_s3";
        }

        if (archivo.isEmpty()) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=sin_archivo";
        }

        String contentType = archivo.getContentType();
        String nombreOriginal = archivo.getOriginalFilename();
        boolean esPdf = "application/pdf".equals(contentType)
                && nombreOriginal != null && nombreOriginal.toLowerCase().endsWith(".pdf");

        if (!esPdf) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=solo_pdf";
        }

        TipoDocumentoEmpleado tipoDocumento = tipoDocumentoEmpleadoRepository.findById(tipo)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de documento no válido"));

        try {
            documentosEmpleadoService.subirDocumento(identificacion, tipoDocumento,
                    nombreOriginal, archivo.getBytes());
            return "redirect:/documentos-empleado/" + identificacion + "?ok=1";
        } catch (IOException | RuntimeException e) {
            return "redirect:/documentos-empleado/" + identificacion + "?error=subida";
        }
    }

    @GetMapping("/documentos-empleado/ver/{id}")
    public void ver(@PathVariable Integer id, HttpServletResponse response,
                     Authentication authentication) throws IOException {

        var doc = documentosEmpleadoService.buscarPorId(id);
        verificarAcceso(doc.getIdentificacionEmpleado(), authentication);

        byte[] contenido = documentosEmpleadoService.descargarDocumento(doc.getS3Key());
        response.setContentType("application/pdf");
        response.getOutputStream().write(contenido);
        response.getOutputStream().flush();
    }

    @PostMapping("/documentos-empleado/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                            @RequestParam String identificacion,
                            @RequestParam(required = false) String s3Key,
                            Authentication authentication) {
        verificarAcceso(identificacion, authentication);

        documentosEmpleadoService.eliminarDocumento(id, s3Key);
        return "redirect:/documentos-empleado/" + identificacion;
    }

    private void verificarAcceso(String identificacion, Authentication authentication) {
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_A"));

        if (!esAdmin && !authentication.getName().equals(identificacion)) {
            throw new AccessDeniedException("No tienes acceso a esta ficha");
        }
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