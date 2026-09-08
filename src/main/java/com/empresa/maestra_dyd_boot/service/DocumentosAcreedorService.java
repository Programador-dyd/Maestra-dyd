package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Acreedores;
import com.empresa.maestra_dyd_boot.model.DocumentosAcreedor;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveClient;
import com.empresa.maestra_dyd_boot.repository.DocumentosAcreedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Service
public class DocumentosAcreedorService {

    private final DocumentosAcreedorRepository documentosAcreedorRepository;
    private final OneDriveClient oneDriveClient;
    private final AcreedoresService acreedoresService;

    public DocumentosAcreedorService(DocumentosAcreedorRepository documentosAcreedorRepository,
                                      OneDriveClient oneDriveClient,
                                      AcreedoresService acreedoresService) {
        this.documentosAcreedorRepository = documentosAcreedorRepository;
        this.oneDriveClient = oneDriveClient;
        this.acreedoresService = acreedoresService;
    }

    public List<DocumentosAcreedor> listarPorAcreedor(String nitAcreedor) {
        return documentosAcreedorRepository.findByNitAcreedorOrderByFechaSubidaDesc(nitAcreedor);
    }

    public DocumentosAcreedor subirDocumento(String accessToken, String nitAcreedor, Long tipoId,
                                              String nombreOriginal, byte[] contenido) {

        Acreedores acreedor = acreedoresService.buscarPorNit(nitAcreedor);

        String extension = "";
        int punto = nombreOriginal.lastIndexOf(".");
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
        }

        String razon = normalizar(acreedor.getRazonSocial());
        String nombreFinal = normalizar(nitAcreedor + "_" + razon) + extension;

        String base = nitAcreedor + "_" + razon;
        String candidato = nombreFinal;
        int contador = 1;
        while (documentosAcreedorRepository.existsByNombreArchivo(candidato)) {
            candidato = base + "(" + contador + ")" + extension;
            contador++;
        }
        nombreFinal = candidato;

        OneDriveClient.ResultadoSubida resultado = oneDriveClient.subirArchivo(accessToken, nombreFinal, contenido);

        DocumentosAcreedor doc = new DocumentosAcreedor();
        doc.setNitAcreedor(nitAcreedor);
        doc.setNombreArchivo(nombreFinal);
        doc.setRutaDocumento("OneDrive");
        doc.setOnedriveId(resultado.onedriveId());
        doc.setOnedriveUrl(resultado.onedriveUrl());
        doc.setTipo(tipoId);

        return documentosAcreedorRepository.save(doc);
    }

    public byte[] descargarDocumento(String accessToken, String onedriveId) {
        return oneDriveClient.descargarArchivo(accessToken, onedriveId);
    }

    public void eliminarDocumento(String accessToken, Integer id, String onedriveId) {
        if (onedriveId != null && !onedriveId.isBlank()) {
            String driveId = oneDriveClient.obtenerDriveId(accessToken);
            oneDriveClient.eliminarArchivo(accessToken, driveId, onedriveId);
        }
        documentosAcreedorRepository.deleteById(id);
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String conGuionesBajos = sinTildes.replace(" ", "_");
        return Pattern.compile("[^a-zA-Z0-9_]").matcher(conGuionesBajos).replaceAll("");
    }
}