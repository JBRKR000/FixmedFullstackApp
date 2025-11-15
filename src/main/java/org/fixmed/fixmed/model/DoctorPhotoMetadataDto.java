package org.fixmed.fixmed.model;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DoctorPhotoMetadataDto {
    String fileName;
    String url;
    String size;
    String checksum;
    FileType type;
}
