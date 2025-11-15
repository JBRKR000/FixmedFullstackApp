package org.fixmed.fixmed.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorPhotoMetadata;
import org.fixmed.fixmed.model.DoctorPhotoMetadataDto;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.FileType;
import org.fixmed.fixmed.repository.DoctorPhotoMetadataRepository;
import org.fixmed.fixmed.repository.DoctorsRepository;
import org.fixmed.fixmed.service.DoctorPhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class DoctorPhotoServiceImpl implements DoctorPhotoService {

    @Value("${doctor.photos.base-url}")
    private String doctorPhotoBaseUrl;

    @Value("${minio.bucket.name}")
    private String bucketName;

    private final DoctorsRepository doctorRepository;
    private final DoctorPhotoMetadataRepository metadataRepository;
    private final MinioClient minioClient;

    @Override
    public void uploadPhoto(String doctorIdStr, MultipartFile file) throws IOException, NoSuchAlgorithmException {
        Long doctorId = parseDoctorId(doctorIdStr);
        validateFile(file);

        FileType type = FileType.getFileType(file.getOriginalFilename());
        if (type == null) {
            throw new IllegalArgumentException("Unsupported file type");
        }

        Doctors doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        String extension = getExtension(file.getOriginalFilename());
        String fileName = doctorId + extension;
        String url = doctorPhotoBaseUrl + "/" + fileName;
        String checksum = calculateChecksum(file);
        String size = formatSize(file.getSize());

        // Upload to MinIO
        try (InputStream inputStream = file.getInputStream()) {
            ensureBucketExists();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }

        // Save metadata to DB
        DoctorPhotoMetadata metadata = DoctorPhotoMetadata.builder()
                .doctor(doctor)
                .fileName(fileName)
                .url(url)
                .size(size)
                .checksum(checksum)
                .type(type)
                .build();

        metadataRepository.save(metadata);
    }

    @Override
    public DoctorPhotoMetadataDto getPhotoMetadata(String doctorIdStr) {
        Long doctorId = parseDoctorId(doctorIdStr);

        DoctorPhotoMetadata metadata = metadataRepository.findByDoctor_Id(doctorId)
                .orElseThrow(() -> new RuntimeException("Metadata not found"));

        return DoctorPhotoMetadataDto.builder()
                .fileName(metadata.getFileName())
                .url(metadata.getUrl())
                .size(metadata.getSize())
                .checksum(metadata.getChecksum())
                .type(metadata.getType())
                .build();
    }

    // --- helpers ---

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > 5 * 1024 * 1024)
            throw new IllegalArgumentException("File must be <= 5MB");
        if (FileType.getFileType(file.getOriginalFilename()) == null)
            throw new IllegalArgumentException("Only JPG and PNG files are allowed");
    }

    private String calculateChecksum(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(file.getInputStream(), digest)) {
            byte[] buffer = new byte[1024];
            while (dis.read(buffer) != -1) ;
        }
        byte[] checksum = digest.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : checksum) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private String getExtension(String filename) {
        int i = filename.lastIndexOf(".");
        return i != -1 ? filename.substring(i).toLowerCase() : "";
    }

    private String formatSize(long bytes) {
        return String.format("%.2f MB", bytes / 1024.0 / 1024.0);
    }

    private Long parseDoctorId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid doctor ID");
        }
    }
}
