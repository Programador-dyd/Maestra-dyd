package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import com.empresa.maestra_dyd_boot.onedrive.OneDriveClient;
import com.empresa.maestra_dyd_boot.repository.DocumentosEmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Service
public class DocumentosEmpleadoService {

    private final DocumentosEmpleadoRepository documentosEmpleadoRepository;
    private final OneDriveClient oneDriveClient;
    private final EmpleadosService empleadosService;

    public DocumentosEmpleadoService(DocumentosEmpleadoRepository documentosEmpleadoRepository,
                                      OneDriveClient oneDriveClient,
                                      EmpleadosService empleadosService) {
        this.documentosEmpleadoRepository = documentosEmpleadoRepository;
        this.oneDriveClient = oneDriveClient;
        this.empleadosService = empleadosService;
    }

    public List<DocumentosEmpleado> listarPorEmpleado(String identificacionEmpleado) {
        return documentosEmpleadoRepository.findByIdentificacionEmpleadoOrderByFechaSubidaDesc(identificacionEmpleado);
    }

    public DocumentosEmpleado subirDocumento(String accessToken, String identificacionEmpleado, TipoDocumentoEmpleado tipo,
                                              String nombreOriginal, byte[] contenido) {

        Empleados empleado = empleadosService.buscarPorIdentificacion(identificacionEmpleado);

        String extension = "";
        int punto = nombreOriginal.lastIndexOf(".");
        if (punto >= 0) {
            extension = nombreOriginal.substring(punto);
        }

        String nombre = normalizar(empleado.getNombreEmpleado());
        String nombreFinal = normalizar(identificacionEmpleado + "_" + nombre) + extension;

        String base = identificacionEmpleado + "_" + nombre;
        String candidato = nombreFinal;
        int contador = 1;
        while (documentosEmpleadoRepository.existsByNombreArchivo(candidato)) {
            candidato = base + "(" + contador + ")" + extension;
            contador++;
        }
        nombreFinal = candidato;

        OneDriveClient.ResultadoSubida resultado = oneDriveClient.subirArchivo(accessToken, nombreFinal, contenido);

        DocumentosEmpleado doc = new DocumentosEmpleado();
        doc.setIdentificacionEmpleado(identificacionEmpleado);
        doc.setNombreArchivo(nombreFinal);
        doc.setRutaDocumento("OneDrive");
        doc.setOnedriveId(resultado.onedriveId());
        doc.setOnedriveUrl(resultado.onedriveUrl());
        doc.setTipo(tipo);

        return documentosEmpleadoRepository.save(doc);
    }

    public byte[] descargarDocumento(String accessToken, String onedriveId) {
        return oneDriveClient.descargarArchivo(accessToken, onedriveId);
    }

    public void eliminarDocumento(String accessToken, Integer id, String onedriveId) {
        if (onedriveId != null && !onedriveId.isBlank()) {
            String driveId = oneDriveClient.obtenerDriveId(accessToken);
            oneDriveClient.eliminarArchivo(accessToken, driveId, onedriveId);
        }
        documentosEmpleadoRepository.deleteById(id);
    }

    private String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String conGuionesBajos = sinTildes.replace(" ", "_");
        return Pattern.compile("[^a-zA-Z0-9_]").matcher(conGuionesBajos).replaceAll("");
    }
}