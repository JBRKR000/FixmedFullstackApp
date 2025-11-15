package org.fixmed.fixmed.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;


import org.fixmed.fixmed.model.DoctorPhotoMetadata;
import org.fixmed.fixmed.model.DoctorPhotoMetadataDto;
import org.springframework.web.multipart.MultipartFile;

public interface DoctorPhotoService {

    void uploadPhoto(String doctorId, MultipartFile file) throws IOException, NoSuchAlgorithmException;

    DoctorPhotoMetadataDto getPhotoMetadata(String doctorId);
}
