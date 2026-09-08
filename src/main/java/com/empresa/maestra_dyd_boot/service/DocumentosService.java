package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Clientes;
import com.empresa.maestra_dyd_boot.model.Documentos;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveClient;
import com.empresa.maestra_dyd_boot.repository.DocumentosRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DocumentosService {

    private final DocumentosRepository documentosRepository;
    private final OneDriveClient oneDriveClient;
    private final ClientesService clientesService;

    public DocumentosService(DocumentosRepository documentosRepository,
                              OneDriveClient oneDriveClient,
                              ClientesService clientesService) {
        this.documentosRepository = documentosRepository;
        this.oneDriveClient = oneDriveClient;
        this.clientesService = clientesService;
    }

    public List<Documentos> listarPorCliente(String nitCliente) {
        return documentosRepository.findByNitClienteOrderByFechaSubidaDesc(nitCliente);
    }

    public Documentos subirDocumento(String accessToken, String nitCliente, Long tipoId,
                                      String nombreOriginal, byte[] contenido) {

        Clientes cliente = clientesService.buscarPorNit(nitCliente);

        String extension = "";
        int punto = nombreOriginal.lastIndexOf(".");
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
        }

        String razon = normalizar(cliente.getRazonSocial());
        String nombreFinal = normalizar(nitCliente + "_" + razon) + extension;

        String base = nitCliente + "_" + razon;
        String candidato = nombreFinal;
        int contador = 1;
        while (documentosRepository.existsByNombreArchivo(candidato)) {
            candidato = base + "(" + contador + ")" + extension;
            contador++;
        }
        nombreFinal = candidato;

        OneDriveClient.ResultadoSubida resultado = oneDriveClient.subirArchivo(accessToken, nombreFinal, contenido);

        Documentos doc = new Documentos();
        doc.setNitCliente(nitCliente);
        doc.setNombreArchivo(nombreFinal);
        doc.setRutaDocumento("OneDrive");
        doc.setOnedriveId(resultado.onedriveId());
        doc.setOnedriveUrl(resultado.onedriveUrl());
        doc.setTipo(tipoId);

        return documentosRepository.save(doc);
    }

    public byte[] descargarDocumento(String accessToken, String onedriveId) {
        return oneDriveClient.descargarArchivo(accessToken, onedriveId);
    }

    public void eliminarDocumento(String accessToken, Integer id, String onedriveId) {
        if (onedriveId != null && !onedriveId.isBlank()) {
            String driveId = oneDriveClient.obtenerDriveId(accessToken);
            oneDriveClient.eliminarArchivo(accessToken, driveId, onedriveId);
        }
        documentosRepository.deleteById(id);
    }

    public void cambiarEstadoDocumentacion(String nitCliente, String estado) {
        Clientes cliente = clientesService.buscarPorNit(nitCliente);
        cliente.setEstadoDocumentacion(estado);
        clientesService.guardar(cliente);
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String conGuionesBajos = sinTildes.replace(" ", "_");
        return Pattern.compile("[^a-zA-Z0-9_]").matcher(conGuionesBajos).replaceAll("");
    }
}