package org.fixmed.fixmed.controller;


import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorPhotoMetadata;
import org.fixmed.fixmed.model.DoctorPhotoMetadataDto;
import org.fixmed.fixmed.service.DoctorPhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/doctors/{doctorId}/photo")
@RequiredArgsConstructor
public class DoctorPhotoController {

    private final DoctorPhotoService doctorPhotoService;

    /**
     * Upload zdjęcia lekarza (JPG lub PNG, max 5MB)
     */
    @PostMapping
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable String doctorId,
            @RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException {
        doctorPhotoService.uploadPhoto(doctorId, file);
        return ResponseEntity.ok().build();
    }

    /**
     * Pobranie metadanych zdjęcia lekarza
     */
    @GetMapping("/metadata")
    public ResponseEntity<DoctorPhotoMetadataDto> getPhotoMetadata(@PathVariable String doctorId) {
        DoctorPhotoMetadataDto metadata = doctorPhotoService.getPhotoMetadata(doctorId);
        return ResponseEntity.ok(metadata);
    }

}

