package com.empresa.maestra_dyd_boot.s3;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3ClientService {

    private final S3Properties s3Properties;

    public S3ClientService(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    public boolean credencialesConfiguradas() {
        return s3Properties.getS3().getBucket() != null && !s3Properties.getS3().getBucket().isBlank();
    }

    private S3Client construirCliente() {
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public record ResultadoSubidaS3(String key, String url) {}

    public ResultadoSubidaS3 subirArchivo(String key, byte[] contenido) {
        try (S3Client cliente = construirCliente()) {
            PutObjectRequest peticion = PutObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucket())
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            cliente.putObject(peticion, RequestBody.fromBytes(contenido));

            String url = "https://" + s3Properties.getS3().getBucket()
                    + ".s3." + s3Properties.getRegion() + ".amazonaws.com/" + key;

            return new ResultadoSubidaS3(key, url);
        }
    }

    public byte[] descargarArchivo(String key) {
        try (S3Client cliente = construirCliente()) {
            GetObjectRequest peticion = GetObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucket())
                    .key(key)
                    .build();

            return cliente.getObjectAsBytes(peticion).asByteArray();
        }
    }

    public void eliminarArchivo(String key) {
        try (S3Client cliente = construirCliente()) {
            DeleteObjectRequest peticion = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucket())
                    .key(key)
                    .build();

            cliente.deleteObject(peticion);
        }
    }
}