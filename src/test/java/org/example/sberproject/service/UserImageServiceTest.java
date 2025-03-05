package org.example.sberproject.service;

import org.example.sberproject.dto.user.ImageUploadDto;
import org.example.sberproject.entity.UserImage;
import org.example.sberproject.exceptions.FileIsEmpty;
import org.example.sberproject.exceptions.UserImageProfileNotFound;
import org.example.sberproject.repository.UserImageRepository;
import org.example.sberproject.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest
class UserImageServiceTest {

    @Autowired
    UserImageService userImageService;

    @MockitoBean
    UserImageRepository userImageRepository;

    @MockitoBean
    UserRepository userRepository;

    @AfterEach
    void afterEach() {
        Mockito.reset(userImageRepository);
        Mockito.reset(userRepository);
    }

    @Test
    void findUserImageById_SuccessfulReturnUserImage(){
        Long userImageId = 1L;
        UserImage expected = new UserImage();
        expected.setId(userImageId);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(expected));

        UserImage actual = userImageService.findUserImageById(userImageId);

        assertEquals(expected, actual);
    }

    @Test
    void findUserImageById_ThrowsResponseStatusException(){
        Long userImageId = 1L;

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            userImageService.findUserImageById(userImageId);
        });
    }

    @Test
    void changeProfileImage_SuccessfulImageChange() throws IOException {
        Long userImageId = 1L;
        String content = "test image content";
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getBytes()).thenReturn(contentBytes);

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setId(userImageId);
        imageUploadDto.setImage(mockMultipartFile);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));
        when(userImageRepository.save(any(UserImage.class))).thenReturn(existingUserImage);

        userImageService.changeProfileImage(imageUploadDto);

        assertEquals(contentBytes, existingUserImage.getProfileImage());
    }

    @Test
    void changeProfileImage_ThrowsFileIsEmptyException() throws IOException {
        Long userImageId = 1L;

        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.isEmpty()).thenReturn(true);

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setId(userImageId);
        imageUploadDto.setImage(mockMultipartFile);

        assertThrows(FileIsEmpty.class, () ->{
            userImageService.changeProfileImage(imageUploadDto);
        });
    }

    @Test
    void changeProfileImage_ThrowsRunTimeException() throws IOException {
        Long userImageId = 1L;

        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getBytes()).thenThrow(new IOException());

        ImageUploadDto uploadDto = new ImageUploadDto();
        uploadDto.setId(userImageId);



        assertThrows(RuntimeException.class, () ->{
            userImageService.changeProfileImage(uploadDto);
        });
    }

    @Test
    void deleteProfileImageAndUploadDefault_SuccessfulDeleteAndReplace() {
        Long userImageId = 1L;
        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);
        byte[] originalImageBytes = "original image".getBytes();
        existingUserImage.setProfileImage(originalImageBytes);

        byte[] defaultProfileImageBytes = userImageService.getDefaultProfileImageBytes();

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));
        when(userImageRepository.save(any(UserImage.class))).thenReturn(existingUserImage);

        userImageService.deleteProfileImageAndUploadDefault(userImageId);
        assertArrayEquals(defaultProfileImageBytes, existingUserImage.getProfileImage());
    }


    @Test
    void deleteProfileImageAndUploadDefault_ThrowsUserImageProfileNotFound(){
        Long userImageId = 1L;
        UserImage existingUserImage = new UserImage();
        byte[] originalImageBytes = userImageService.getDefaultProfileImageBytes();
        existingUserImage.setId(userImageId);
        existingUserImage.setProfileImage(originalImageBytes);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        assertThrows(UserImageProfileNotFound.class, () ->{
            userImageService.deleteProfileImageAndUploadDefault(userImageId);
        });

    }

    @Test
    void getProfileByUserId_Successful() {
        Long userId = 1L;
        UserImage userImage = new UserImage();
        byte[] existByte = "ExampleBytes".getBytes();
        userImage.setProfileImage(existByte);
        userImage.setId(userId);

        when(userImageRepository.findById(userId)).thenReturn(Optional.of(userImage));

        byte[] result = userImageService.getProfileByUserId(userId);

        assertEquals(existByte, result);
    }

    @Test
    void getProfileByUserId_ThrowsResponseStatusException(){
        Long userId = 1L;
        UserImage userImage = new UserImage();

        when(userImageRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
           userImageService.getProfileByUserId(userId);
        });
    }

}