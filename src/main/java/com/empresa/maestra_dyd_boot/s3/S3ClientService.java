package com.empresa.maestra_dyd_boot.s3;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;

@Service
public class S3ClientService {

    private final S3Properties s3Properties;

    public S3ClientService(S3Properties s3Properties) {
        this.s3Properties = s3Properties;
    }

    public boolean credencialesConfiguradas() {
        return s3Properties.getAccessKeyId() != null && !s3Properties.getAccessKeyId().isBlank()
                && s3Properties.getSecretAccessKey() != null && !s3Properties.getSecretAccessKey().isBlank()
                && s3Properties.getS3().getBucketName() != null && !s3Properties.getS3().getBucketName().isBlank();
    }

    private S3Client construirCliente() {
        AwsBasicCredentials credenciales = AwsBasicCredentials.create(
                s3Properties.getAccessKeyId(),
                s3Properties.getSecretAccessKey()
        );

        return S3Client.builder()
                .region(Region.of(s3Properties.getS3().getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credenciales))
                .build();
    }

    public record ResultadoSubidaS3(String key, String url) {}

    public ResultadoSubidaS3 subirArchivo(String key, byte[] contenido) {
        try (S3Client cliente = construirCliente()) {
            PutObjectRequest peticion = PutObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucketName())
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            cliente.putObject(peticion, RequestBody.fromBytes(contenido));

            String url = "https://" + s3Properties.getS3().getBucketName()
                    + ".s3." + s3Properties.getS3().getRegion() + ".amazonaws.com/" + key;

            return new ResultadoSubidaS3(key, url);
        }
    }

    public byte[] descargarArchivo(String key) {
        try (S3Client cliente = construirCliente()) {
            GetObjectRequest peticion = GetObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucketName())
                    .key(key)
                    .build();

            return cliente.getObjectAsBytes(peticion).asByteArray();
        }
    }

    public void eliminarArchivo(String key) {
        try (S3Client cliente = construirCliente()) {
            DeleteObjectRequest peticion = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getS3().getBucketName())
                    .key(key)
                    .build();

            cliente.deleteObject(peticion);
        }
    }
}