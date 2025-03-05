package org.example.sberproject.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageUploadDto {
    private Long id;
    private MultipartFile image;
}
