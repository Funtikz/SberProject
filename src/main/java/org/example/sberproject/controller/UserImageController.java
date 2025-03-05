package org.example.sberproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.sberproject.dto.user.ImageUploadDto;
import org.example.sberproject.entity.UserImage;
import org.example.sberproject.service.UserImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/images")
@Repository
@RequiredArgsConstructor
public class UserImageController {

    private final UserImageService imageService;

    @PutMapping("/update")
    public ResponseEntity<String> updateImage(@RequestParam("userId") Long userId,
                                              @RequestParam("images")MultipartFile image){
        ImageUploadDto imageUpdateDto = new ImageUploadDto();
        imageUpdateDto.setId(userId);
        imageUpdateDto.setImage(image);
        imageService.changeProfileImage(imageUpdateDto);
        return ResponseEntity.ok("Картинка успешна обновлена!");
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable Long userId) {
        try {
            byte[] imageBytes = imageService.getProfileByUserId(userId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteProfileAndUploadDefault(@PathVariable("userId") Long userId){
        imageService.deleteProfileImageAndUploadDefault(userId);
        return ResponseEntity.ok("Изображения профиля удалено");
    }

}
