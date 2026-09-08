package com.empresa.maestra_dyd_boot.controller;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveTokenService;
import com.empresa.maestra_dyd_boot.service.ProveedoresService;
import com.empresa.maestra_dyd_boot.service.DocumentosProveedorService;
import com.empresa.maestra_dyd_boot.service.TipoDocumentoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class DocumentosProveedorController {

    private final DocumentosProveedorService documentosProveedorService;
    private final ProveedoresService proveedoresService;
    private final TipoDocumentoService tipoDocumentoService;
    private final OneDriveTokenService oneDriveTokenService;

    public DocumentosProveedorController(DocumentosProveedorService documentosProveedorService,
                                          ProveedoresService proveedoresService,
                                          TipoDocumentoService tipoDocumentoService,
                                          OneDriveTokenService oneDriveTokenService) {
        this.documentosProveedorService = documentosProveedorService;
        this.proveedoresService = proveedoresService;
        this.tipoDocumentoService = tipoDocumentoService;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/documentos-proveedor/{nitProveedor}")
    public String verDocumentos(@PathVariable String nitProveedor, Model model) {
        Proveedores proveedor = proveedoresService.buscarPorNit(nitProveedor);
        var tiposDocumento = tipoDocumentoService.listarTodos();

        var nombresPorId = tiposDocumento.stream()
                .collect(java.util.stream.Collectors.toMap(
                        td -> td.getId(),
                        td -> td.getTipo()
                ));

        model.addAttribute("proveedor", proveedor);
        model.addAttribute("documentos", documentosProveedorService.listarPorProveedor(nitProveedor));
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("nombresTipoDocumento", nombresPorId);

        return "documentosProveedor";
    }

    @PostMapping("/documentos-proveedor/{nitProveedor}/subir")
    public String subir(@PathVariable String nitProveedor,
                         @RequestParam Long tipo,
                         @RequestParam MultipartFile archivo) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-proveedor/" + nitProveedor + "?error=sin_token";
        }

        if (archivo.isEmpty()) {
            return "redirect:/documentos-proveedor/" + nitProveedor + "?error=sin_archivo";
        }

        try {
            documentosProveedorService.subirDocumento(token.get(), nitProveedor, tipo,
                    archivo.getOriginalFilename(), archivo.getBytes());
            return "redirect:/documentos-proveedor/" + nitProveedor + "?ok=1";
        } catch (IOException | RuntimeException e) {
            return "redirect:/documentos-proveedor/" + nitProveedor + "?error=subida";
        }
    }

    @GetMapping("/documentos-proveedor/ver/{onedriveId}")
    public void ver(@PathVariable String onedriveId, HttpServletResponse response) throws IOException {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Conexión con OneDrive no disponible");
            return;
        }

        byte[] contenido = documentosProveedorService.descargarDocumento(token.get(), onedriveId);
        response.setContentType("application/octet-stream");
        response.getOutputStream().write(contenido);
        response.getOutputStream().flush();
    }

    @PostMapping("/documentos-proveedor/{id}/eliminar")
    public String eliminar(@PathVariable Integer id,
                            @RequestParam String nitProveedor,
                            @RequestParam(required = false) String onedriveId) {

        Optional<String> token = oneDriveTokenService.obtenerAccessTokenValido();
        if (token.isEmpty()) {
            return "redirect:/documentos-proveedor/" + nitProveedor + "?error=sin_token";
        }

        documentosProveedorService.eliminarDocumento(token.get(), id, onedriveId);
        return "redirect:/documentos-proveedor/" + nitProveedor;
    }

}