package com.empresa.maestra_dyd_boot.onedrive;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class OneDriveTokenService {

    private static final int ID_UNICO = 1;

    private final OneDriveConexionRepository conexionRepository;
    private final OneDriveProperties oneDriveProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public OneDriveTokenService(OneDriveConexionRepository conexionRepository, OneDriveProperties oneDriveProperties) {
        this.conexionRepository = conexionRepository;
        this.oneDriveProperties = oneDriveProperties;
    }

    public boolean hayConexionActiva() {
        return conexionRepository.findById(ID_UNICO).isPresent();
    }

    /**
     * Guarda la conexión inicial, justo después de que un usuario autoriza por primera vez.
     */
    public void guardarConexionInicial(String accessToken, String refreshToken, int expiraEnSegundos) {
    OneDriveConexion conexion = conexionRepository.findById(ID_UNICO).orElse(new OneDriveConexion());
    conexion.setAccessToken(accessToken);
    conexion.setRefreshToken(refreshToken);
    conexion.setFechaExpiracionAccessToken(LocalDateTime.now().plusSeconds(expiraEnSegundos));
    conexion.setFechaActualizacion(LocalDateTime.now());
    conexionRepository.save(conexion);
    }

    /**
     * Devuelve un access_token siempre válido, renovándolo automáticamente si ya expiró
     * (o está a punto de hacerlo) en el momento en que se solicita.
     */
    public Optional<String> obtenerAccessTokenValido() {
        Optional<OneDriveConexion> conexionOpt = conexionRepository.findById(ID_UNICO);

        if (conexionOpt.isEmpty()) {
            return Optional.empty();
        }

        OneDriveConexion conexion = conexionOpt.get();

        if (conexion.estaPorExpirar()) {
            return renovarToken(conexion);
        }

        return Optional.of(conexion.getAccessToken());
    }

    /**
     * Tarea programada: revisa cada 30 minutos si el token está por expirar, y lo renueva
     * proactivamente en segundo plano, sin esperar a que alguien lo necesite.
     */
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void renovarTokenEnSegundoPlano() {
        conexionRepository.findById(ID_UNICO).ifPresent(conexion -> {
            if (conexion.estaPorExpirar()) {
                renovarToken(conexion);
            }
        });
    }

    private Optional<String> renovarToken(OneDriveConexion conexion) {
        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", oneDriveProperties.getClientId());
            params.add("client_secret", oneDriveProperties.getClientSecret());
            params.add("grant_type", "refresh_token");
            params.add("refresh_token", conexion.getRefreshToken());
            params.add("scope", "offline_access Files.ReadWrite.All User.Read");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> peticion = new HttpEntity<>(params, headers);

            String tokenUrl = "https://login.microsoftonline.com/" + oneDriveProperties.getTenantId() + "/oauth2/v2.0/token";

            ResponseEntity<Map<String, Object>> respuesta = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, peticion,
                    new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> cuerpo = respuesta.getBody();

            if (cuerpo == null || !cuerpo.containsKey("access_token")) {
                return Optional.empty();
            }

            String nuevoAccessToken = (String) cuerpo.get("access_token");
            String nuevoRefreshToken = (String) cuerpo.getOrDefault("refresh_token", conexion.getRefreshToken());
            int expiraEnSegundos = (int) cuerpo.getOrDefault("expires_in", 3600);

            conexion.setAccessToken(nuevoAccessToken);
            conexion.setRefreshToken(nuevoRefreshToken);
            conexion.setFechaExpiracionAccessToken(LocalDateTime.now().plusSeconds(expiraEnSegundos));
            conexion.setFechaActualizacion(LocalDateTime.now());
            conexionRepository.save(conexion);

            return Optional.of(nuevoAccessToken);

        } catch (Exception e) {
            System.out.println("Error al renovar el token de OneDrive: " + e.getMessage());
            return Optional.empty();
        }
    }
}