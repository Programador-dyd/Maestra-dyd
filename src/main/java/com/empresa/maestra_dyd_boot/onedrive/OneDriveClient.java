package com.empresa.maestra_dyd_boot.onedrive;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Service
public class OneDriveClient {

    private static final String USER_PRINCIPAL_NAME = "nubedyd@dulcesydulces01.onmicrosoft.com";
    private static final String CARPETA_DOCUMENTOS = "DocumentosClientes";

    private final RestTemplate restTemplate = new RestTemplate();

    public String obtenerDriveId(String accessToken) {
        String url = "https://graph.microsoft.com/v1.0/users/" + USER_PRINCIPAL_NAME + "/drive";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<Map<String, Object>> respuesta = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers),
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

        return (String) respuesta.getBody().get("id");
    }

    @SuppressWarnings("unchecked")
    private String asegurarCarpeta(String accessToken, String driveId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        String urlListar = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/root/children";
        ResponseEntity<Map<String, Object>> listado = restTemplate.exchange(
                urlListar, HttpMethod.GET, new HttpEntity<>(headers),
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

        List<Map<String, Object>> items = (List<Map<String, Object>>) listado.getBody().get("value");
        for (Map<String, Object> item : items) {
            if (item.containsKey("folder") && CARPETA_DOCUMENTOS.equals(item.get("name"))) {
                return (String) item.get("id");
            }
        }

        // No existe: la creamos
        HttpHeaders headersCrear = new HttpHeaders();
        headersCrear.setBearerAuth(accessToken);
        headersCrear.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "name", CARPETA_DOCUMENTOS,
                "folder", Map.of(),
                "@microsoft.graph.conflictBehavior", "rename"
        );

        String urlCrear = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/root/children";
        ResponseEntity<Map<String, Object>> creada = restTemplate.exchange(
                urlCrear, HttpMethod.POST, new HttpEntity<>(body, headersCrear),
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

        return (String) creada.getBody().get("id");
    }

    public record ResultadoSubida(String onedriveId, String onedriveUrl) {}

    public ResultadoSubida subirArchivo(String accessToken, String nombreArchivo, byte[] contenido) {
        String driveId = obtenerDriveId(accessToken);
        String carpetaId = asegurarCarpeta(accessToken, driveId);

        String uploadUrl = UriComponentsBuilder
                .fromUriString("https://graph.microsoft.com/v1.0/drives/" + driveId + "/items/" + carpetaId + ":/{nombre}:/content")
                .buildAndExpand(nombreArchivo)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ResponseEntity<Map<String, Object>> respuesta = restTemplate.exchange(
                uploadUrl, HttpMethod.PUT, new HttpEntity<>(contenido, headers),
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> cuerpo = respuesta.getBody();
        String id = cuerpo != null ? (String) cuerpo.getOrDefault("id", "SIN_ID") : "SIN_ID";
        String webUrl = cuerpo != null ? (String) cuerpo.getOrDefault("webUrl", "SIN_URL") : "SIN_URL";

        return new ResultadoSubida(id, webUrl);
    }

    public byte[] descargarArchivo(String accessToken, String itemId) {
        String url = "https://graph.microsoft.com/v1.0/me/drive/items/" + itemId + "/content";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        ResponseEntity<byte[]> respuesta = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), byte[].class);

        return respuesta.getBody();
    }

    public void eliminarArchivo(String accessToken, String driveId, String archivoId) {
        String url = "https://graph.microsoft.com/v1.0/drives/" + driveId + "/items/" + archivoId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
    }
}