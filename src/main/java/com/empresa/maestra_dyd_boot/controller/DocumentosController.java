package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Clientes;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveTokenService;
import com.empresa.maestra_dyd_boot.service.ClientesService;
import com.empresa.maestra_dyd_boot.service.DocumentosService;
import com.empresa.maestra_dyd_boot.service.TipoDocumentoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class DocumentosController {

    private final DocumentosService documentosService;
    private final ClientesService clientesService;
    private final TipoDocumentoService tipoDocumentoService;
    private final OneDriveTokenService oneDriveTokenService;

    public DocumentosController(DocumentosService documentosService,
                                 ClientesService clientesService,
                                 TipoDocumentoService tipoDocumentoService,
                                 OneDriveTokenService oneDriveTokenService) {
        this.documentosService = documentosService;
        this.clientesService = clientesService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/documentos/{nitCliente}")
    public String verDocumentos(@PathVariable String nitCliente, Model model) {
        Clientes cliente = clientesService.buscarPorNit(nitCliente);
        var tiposDocumento = tipoDocumentoService.listarTodos();

        var nombresPorId = tiposDocumento.stream()
                .collect(java.util.stream.Collectors.toMap(
                        td -> td.getId(),
                        td -> td.getTipo()
                ));

        model.addAttribute("cliente", cliente);
        model.addAttribute("documentos", documentosService.listarPorCliente(nitCliente));
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("nombresTipoDocumento", nombresPorId);

        return "documentosCliente";
    }

    @PostMapping("/documentos/{nitCliente}/subir")
    public String subir(@PathVariable String nitCliente,
                         @RequestParam Long tipo,
                         @RequestParam MultipartFile archivo) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos/" + nitCliente + "?error=sin_token";
        }

        if (archivo.isEmpty()) {
            return "redirect:/documentos/" + nitCliente + "?error=sin_archivo";
        }

        try {
            documentosService.subirDocumento(token.get(), nitCliente, tipo,
                    archivo.getOriginalFilename(), archivo.getBytes());
            return "redirect:/documentos/" + nitCliente + "?ok=1";
        } catch (IOException | RuntimeException e) {
            return "redirect:/documentos/" + nitCliente + "?error=subida";
        }
    }

    @GetMapping("/documentos/ver/{onedriveId}")
    public void ver(@PathVariable String onedriveId, HttpServletResponse response) throws IOException {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Conexión con OneDrive no disponible");
            return;
        }

        byte[] contenido = documentosService.descargarDocumento(token.get(), onedriveId);
        response.setContentType("application/octet-stream");
        response.getOutputStream().write(contenido);
        response.getOutputStream().flush();
    }

    @PostMapping("/documentos/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                            @RequestParam String nitCliente,
                            @RequestParam(required = false) String onedriveId) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos/" + nitCliente + "?error=sin_token";
        }

        documentosService.eliminarDocumento(token.get(), id, onedriveId);
        return "redirect:/documentos/" + nitCliente;
    }

    @PostMapping("/documentos/{nitCliente}/estado")
    public String cambiarEstado(@PathVariable String nitCliente, @RequestParam String estado) {
        documentosService.cambiarEstadoDocumentacion(nitCliente, estado);
        return "redirect:/documentos/" + nitCliente;
    }

}