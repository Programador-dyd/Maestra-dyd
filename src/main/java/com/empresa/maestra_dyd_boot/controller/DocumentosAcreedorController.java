package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Acreedores;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveTokenService;
import com.empresa.maestra_dyd_boot.service.AcreedoresService;
import com.empresa.maestra_dyd_boot.service.DocumentosAcreedorService;
import com.empresa.maestra_dyd_boot.service.TipoDocumentoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class DocumentosAcreedorController {

    private final DocumentosAcreedorService documentosAcreedorService;
    private final AcreedoresService acreedoresService;
    private final TipoDocumentoService tipoDocumentoService;
    private final OneDriveTokenService oneDriveTokenService;

    public DocumentosAcreedorController(DocumentosAcreedorService documentosAcreedorService,
                                         AcreedoresService acreedoresService,
                                         TipoDocumentoService tipoDocumentoService,
                                         OneDriveTokenService oneDriveTokenService) {
        this.documentosAcreedorService = documentosAcreedorService;
        this.acreedoresService = acreedoresService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/documentos-acreedor/{nitAcreedor}")
    public String verDocumentos(@PathVariable String nitAcreedor, Model model) {
        Acreedores acreedor = acreedoresService.buscarPorNit(nitAcreedor);
        var tiposDocumento = tipoDocumentoService.listarTodos();

        var nombresPorId = tiposDocumento.stream()
                .collect(java.util.stream.Collectors.toMap(
                        td -> td.getId(),
                        td -> td.getTipo()
                ));

        model.addAttribute("acreedor", acreedor);
        model.addAttribute("documentos", documentosAcreedorService.listarPorAcreedor(nitAcreedor));
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("nombresTipoDocumento", nombresPorId);

        return "documentosAcreedor";
    }

    @PostMapping("/documentos-acreedor/{nitAcreedor}/subir")
    public String subir(@PathVariable String nitAcreedor,
                         @RequestParam Long tipo,
                         @RequestParam MultipartFile archivo) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-acreedor/" + nitAcreedor + "?error=sin_token";
        }

        if (archivo.isEmpty()) {
            return "redirect:/documentos-acreedor/" + nitAcreedor + "?error=sin_archivo";
        }

        try {
            documentosAcreedorService.subirDocumento(token.get(), nitAcreedor, tipo,
                    archivo.getOriginalFilename(), archivo.getBytes());
            return "redirect:/documentos-acreedor/" + nitAcreedor + "?ok=1";
        } catch (IOException | RuntimeException e) {
            return "redirect:/documentos-acreedor/" + nitAcreedor + "?error=subida";
        }
    }

    @GetMapping("/documentos-acreedor/ver/{onedriveId}")
    public void ver(@PathVariable String onedriveId, HttpServletResponse response) throws IOException {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Conexión con OneDrive no disponible");
            return;
        }

        byte[] contenido = documentosAcreedorService.descargarDocumento(token.get(), onedriveId);
        response.setContentType("application/octet-stream");
        response.getOutputStream().write(contenido);
        response.getOutputStream().flush();
    }

    @PostMapping("/documentos-acreedor/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                            @RequestParam String nitAcreedor,
                            @RequestParam(required = false) String onedriveId) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-acreedor/" + nitAcreedor + "?error=sin_token";
        }

        documentosAcreedorService.eliminarDocumento(token.get(), id, onedriveId);
        return "redirect:/documentos-acreedor/" + nitAcreedor;
    }

}