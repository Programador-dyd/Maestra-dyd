package com.empresa.maestra_dyd_boot.service;

import com.empresa.maestra_dyd_boot.model.Empleados;
import com.empresa.maestra_dyd_boot.model.TipoDocumentoEmpleado;
import com.empresa.maestra_dyd_boot.model.DocumentosEmpleado;
import com.empresa.maestra_dyd_boot.repository.DocumentosEmpleadoRepository;
import com.empresa.maestra_dyd_boot.s3.S3ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Service
public class DocumentosEmpleadoService {

    private final DocumentosEmpleadoRepository documentosEmpleadoRepository;
    private final S3ClientService s3ClientService;
    private final EmpleadosService empleadosService;

    public DocumentosEmpleadoService(DocumentosEmpleadoRepository documentosEmpleadoRepository,
                                      S3ClientService s3ClientService,
                                      EmpleadosService empleadosService) {
        this.documentosEmpleadoRepository = documentosEmpleadoRepository;
        this.s3ClientService = s3ClientService;
        this.empleadosService = empleadosService;
    }

    public boolean s3Disponible() {
        return s3ClientService.credencialesConfiguradas();
    }

    public List<DocumentosEmpleado> listarPorEmpleado(String identificacionEmpleado) {
        return documentosEmpleadoRepository.findByIdentificacionEmpleadoOrderByFechaSubidaDesc(identificacionEmpleado);
    }

    public DocumentosEmpleado buscarPorId(Integer id) {
        return documentosEmpleadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Documento no encontrado: " + id));
    }

    public DocumentosEmpleado subirDocumento(String identificacionEmpleado, TipoDocumentoEmpleado tipo,
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

        String key = "empleados/" + identificacionEmpleado + "/" + nombreFinal;

        S3ClientService.ResultadoSubidaS3 resultado = s3ClientService.subirArchivo(key, contenido);

        DocumentosEmpleado doc = new DocumentosEmpleado();
        doc.setIdentificacionEmpleado(identificacionEmpleado);
        doc.setNombreArchivo(nombreFinal);
        doc.setRutaDocumento("S3");
        doc.setS3Key(resultado.key());
        doc.setS3Url(resultado.url());
        doc.setTipo(tipo);

        return documentosEmpleadoRepository.save(doc);
    }

    public byte[] descargarDocumento(String s3Key) {
        return s3ClientService.descargarArchivo(s3Key);
    }

    public void eliminarDocumento(Integer id, String s3Key) {
        if (s3Key != null && !s3Key.isBlank()) {
            s3ClientService.eliminarArchivo(s3Key);
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