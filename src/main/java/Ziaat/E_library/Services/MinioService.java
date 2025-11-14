package Ziaat.E_library.Services;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    /**
     * Upload file from MultipartFile
     */
    public String uploadFile(MultipartFile file, String bucket) {
        try {
            String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            // Ensure bucket exists
            ensureBucketExists(bucket);

            // Upload file
            try (InputStream is = file.getInputStream()) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(is, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build());
            }

            return bucket + "/" + objectName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    /**
     * Upload file from base64 string
     * @param base64Data The base64 encoded file data (without data:image/png;base64, prefix)
     * @param fileName Original filename
     * @param contentType MIME type (e.g., image/jpeg, application/pdf)
     * @param bucket MinIO bucket name
     * @return The path to the uploaded file (bucket/objectName)
     */
    public String uploadFileFromBase64(String base64Data, String fileName, String contentType, String bucket) {
        try {
            System.out.println("=== Starting base64 upload ===");
            System.out.println("Filename: " + fileName);
            System.out.println("ContentType: " + contentType);
            System.out.println("Bucket: " + bucket);
            System.out.println("Base64 data length: " + (base64Data != null ? base64Data.length() : "null"));

            // Set default content type if empty
            if (contentType == null || contentType.isEmpty()) {
                contentType = getContentTypeFromFilename(fileName);
                System.out.println("ContentType was empty, using: " + contentType);
            }

            // Decode base64
            byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

            // Generate unique filename
            String objectName = UUID.randomUUID() + "-" + fileName;

            System.out.println("Uploading to MinIO:");
            System.out.println("  Bucket: " + bucket);
            System.out.println("  Object: " + objectName);
            System.out.println("  Size: " + decodedBytes.length + " bytes");
            System.out.println("  ContentType: " + contentType);

            // Ensure bucket exists
            ensureBucketExists(bucket);

            // Upload file
            try (ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes)) {
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .stream(bis, decodedBytes.length, -1)
                        .contentType(contentType)
                        .build());
            }

            String result = objectName;
            System.out.println("Upload successful: " + result);
            return result;

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid base64 data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid base64 data: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("Failed to upload file to MinIO: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to upload file to MinIO: " + e.getMessage(), e);
        }
    }





    /** ✅ Download object from MinIO */
    public InputStream getObject(String bucket, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to download object from MinIO", e);
        }
    }

    /** ✅ Remove object from MinIO */
    public boolean removeObject(String bucket, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to remove object from MinIO", e);
        }
    }

    /** Generate a presigned download URL */
    public String getPresignedUrl(String bucket, String objectName, int expiryMinutes) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectName)
                            .expiry(expiryMinutes * 60)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    /** Ensure bucket exists */
    private void ensureBucketExists(String bucket) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /** Helper: Guess content type */
    private String getContentTypeFromFilename(String filename) {
        if (filename == null) return "application/octet-stream";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".pdf")) return "application/pdf";
        return "application/octet-stream";
    }

}