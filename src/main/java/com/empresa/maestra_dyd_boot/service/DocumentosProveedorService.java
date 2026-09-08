package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Proveedores;
import com.empresa.maestra_dyd_boot.model.DocumentosProveedor;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveClient;
import com.empresa.maestra_dyd_boot.repository.DocumentosProveedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Service
public class DocumentosProveedorService {

    private final DocumentosProveedorRepository documentosProveedorRepository;
    private final OneDriveClient oneDriveClient;
    private final ProveedoresService proveedoresService;

    public DocumentosProveedorService(DocumentosProveedorRepository documentosProveedorRepository,
                                       OneDriveClient oneDriveClient,
                                       ProveedoresService proveedoresService) {
        this.documentosProveedorRepository = documentosProveedorRepository;
        this.oneDriveClient = oneDriveClient;
        this.proveedoresService = proveedoresService;
    }

    public List<DocumentosProveedor> listarPorProveedor(String nitProveedor) {
        return documentosProveedorRepository.findByNitProveedorOrderByFechaSubidaDesc(nitProveedor);
    }

    public DocumentosProveedor subirDocumento(String accessToken, String nitProveedor, Long tipoId,
                                               String nombreOriginal, byte[] contenido) {

        Proveedores proveedor = proveedoresService.buscarPorNit(nitProveedor);

        String extension = "";
        int punto = nombreOriginal.lastIndexOf(".");
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
        }

        String razon = normalizar(proveedor.getRazonSocial());
        String nombreFinal = normalizar(nitProveedor + "_" + razon) + extension;

        String base = nitProveedor + "_" + razon;
        String candidato = nombreFinal;
        int contador = 1;
        while (documentosProveedorRepository.existsByNombreArchivo(candidato)) {
            candidato = base + "(" + contador + ")" + extension;
            contador++;
        }
        nombreFinal = candidato;

        OneDriveClient.ResultadoSubida resultado = oneDriveClient.subirArchivo(accessToken, nombreFinal, contenido);

        DocumentosProveedor doc = new DocumentosProveedor();
        doc.setNitProveedor(nitProveedor);
        doc.setNombreArchivo(nombreFinal);
        doc.setRutaDocumento("OneDrive");
        doc.setOnedriveId(resultado.onedriveId());
        doc.setOnedriveUrl(resultado.onedriveUrl());
        doc.setTipo(tipoId);

        return documentosProveedorRepository.save(doc);
    }

    public byte[] descargarDocumento(String accessToken, String onedriveId) {
        return oneDriveClient.descargarArchivo(accessToken, onedriveId);
    }

    public void eliminarDocumento(String accessToken, Integer id, String onedriveId) {
        if (onedriveId != null && !onedriveId.isBlank()) {
            String driveId = oneDriveClient.obtenerDriveId(accessToken);
            oneDriveClient.eliminarArchivo(accessToken, driveId, onedriveId);
        }
        documentosProveedorRepository.deleteById(id);
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String conGuionesBajos = sinTildes.replace(" ", "_");
        return Pattern.compile("[^a-zA-Z0-9_]").matcher(conGuionesBajos).replaceAll("");
    }
}