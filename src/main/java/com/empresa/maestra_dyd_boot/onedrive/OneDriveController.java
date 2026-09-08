package com.empresa.maestra_dyd_boot.onedrive;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Controller
public class OneDriveController {

    private final OneDriveProperties oneDriveProperties;
    private final OneDriveTokenService oneDriveTokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    public OneDriveController(OneDriveProperties oneDriveProperties, OneDriveTokenService oneDriveTokenService) {
        this.oneDriveProperties = oneDriveProperties;
        this.oneDriveTokenService = oneDriveTokenService;
    }

    @GetMapping("/onedrive/conectar")
    public ResponseEntity<Void> iniciarConexion() {
        if (!credencialesConfiguradas()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/clientes?onedriveError=configuracion")
                    .build();
        }

        String url = UriComponentsBuilder
                .fromUriString("https://login.microsoftonline.com/" + oneDriveProperties.getTenantId() + "/oauth2/v2.0/authorize")
                .queryParam("client_id", oneDriveProperties.getClientId())
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", oneDriveProperties.getRedirectUri())
                .queryParam("response_mode", "query")
                .queryParam("scope", "openid profile offline_access User.Read Files.ReadWrite")
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }

    @GetMapping("/oauth/callback")
    public String recibirCallback(@RequestParam(required = false) String code, HttpServletRequest request) {

        if (code == null || code.isBlank()) {
            return "redirect:/clientes?onedriveError=sin_codigo";
        }

        if (!credencialesConfiguradas()) {
            return "redirect:/clientes?onedriveError=configuracion";
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", oneDriveProperties.getClientId());
        params.add("scope", "offline_access Files.ReadWrite.All User.Read");
        params.add("code", code);
        params.add("redirect_uri", oneDriveProperties.getRedirectUri());
        params.add("grant_type", "authorization_code");
        params.add("client_secret", oneDriveProperties.getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> peticion = new HttpEntity<>(params, headers);

        String tokenUrl = "https://login.microsoftonline.com/" + oneDriveProperties.getTenantId() + "/oauth2/v2.0/token";

        ResponseEntity<Map<String, Object>> respuestaEntity = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, peticion,
                new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> respuesta = respuestaEntity.getBody();

        if (respuesta == null || !respuesta.containsKey("access_token") || !respuesta.containsKey("refresh_token")) {
            return "redirect:/clientes?onedriveError=token";
        }

        String accessToken = (String) respuesta.get("access_token");
        String refreshToken = (String) respuesta.get("refresh_token");
        int expiraEnSegundos = (int) respuesta.getOrDefault("expires_in", 3600);

        oneDriveTokenService.guardarConexionInicial(accessToken, refreshToken, expiraEnSegundos);

        return "redirect:/clientes?onedriveOk=1";
    }

    private boolean credencialesConfiguradas() {
        return oneDriveProperties.getTenantId() != null && !oneDriveProperties.getTenantId().isBlank()
                && oneDriveProperties.getClientId() != null && !oneDriveProperties.getClientId().isBlank()
                && oneDriveProperties.getClientSecret() != null && !oneDriveProperties.getClientSecret().isBlank();
    }

}