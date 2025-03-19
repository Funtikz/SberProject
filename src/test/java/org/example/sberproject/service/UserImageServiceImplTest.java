package org.example.sberproject.service;

import org.example.sberproject.dto.user.ImageUploadDto;
import org.example.sberproject.entity.UserImage;
import org.example.sberproject.exceptions.FileIsEmpty;
import org.example.sberproject.exceptions.UserImageProfileNotFound;
import org.example.sberproject.repository.UserImageRepository;
import org.example.sberproject.service.impl.UserImageServiceImpl;
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
import static org.mockito.Mockito.*;

@SpringBootTest
class UserImageServiceImplTest {

    @Autowired
    UserImageServiceImpl userImageServiceImpl;

    @MockitoBean
    UserImageRepository userImageRepository;

    @AfterEach
    void afterEach() {
        Mockito.reset(userImageRepository);
    }

    @Test
    void findUserImageById_Success() {
        Long userImageId = 1L;
        UserImage expected = new UserImage();
        expected.setId(userImageId);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(expected));

        UserImage actual = userImageServiceImpl.findUserImageById(userImageId);

        assertEquals(expected, actual);
    }

    @Test
    void findUserImageById_ThrowsResponseStatusException() {
        Long userImageId = 1L;

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userImageServiceImpl.findUserImageById(userImageId));
    }

    @Test
    void changeProfileImage_Success() throws IOException {
        Long userImageId = 1L;
        byte[] contentBytes = "test image content".getBytes(StandardCharsets.UTF_8);

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

        userImageServiceImpl.changeProfileImage(imageUploadDto);

        assertArrayEquals(contentBytes, existingUserImage.getProfileImage());
    }

    @Test
    void changeProfileImage_ThrowsFileIsEmptyException() {
        Long userImageId = 1L;


        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setId(userImageId);
        imageUploadDto.setImage(mockMultipartFile);

        assertThrows(FileIsEmpty.class, () -> userImageServiceImpl.changeProfileImage(imageUploadDto));
    }

    @Test
    void changeProfileImage_ThrowsIOException() throws IOException {
        Long userImageId = 1L;

        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        MultipartFile mockMultipartFile = mock(MultipartFile.class);
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getBytes()).thenThrow(new IOException());

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setId(userImageId);
        imageUploadDto.setImage(mockMultipartFile);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        assertThrows(RuntimeException.class, () -> userImageServiceImpl.changeProfileImage(imageUploadDto));
    }

    @Test
    void deleteProfileImageAndUploadDefault_Success() {
        Long userImageId = 1L;
        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);
        existingUserImage.setProfileImage("original image".getBytes());

        byte[] defaultProfileImageBytes = userImageServiceImpl.getDefaultProfileImageBytes();

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        userImageServiceImpl.deleteProfileImageAndUploadDefault(userImageId);

        assertArrayEquals(defaultProfileImageBytes, existingUserImage.getProfileImage());
        verify(userImageRepository, times(1)).save(existingUserImage);
    }

    @Test
    void deleteProfileImageAndUploadDefault_ThrowsUserImageProfileNotFound() {
        Long userImageId = 1L;
        UserImage existingUserImage = new UserImage();
        byte[] defaultImage = userImageServiceImpl.getDefaultProfileImageBytes();
        existingUserImage.setId(userImageId);
        existingUserImage.setProfileImage(defaultImage);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        assertThrows(UserImageProfileNotFound.class, () -> userImageServiceImpl.deleteProfileImageAndUploadDefault(userImageId));
    }

    @Test
    void getProfileByUserId_Success() {
        Long userId = 1L;
        UserImage userImage = new UserImage();
        byte[] imageBytes = "ExampleBytes".getBytes();
        userImage.setProfileImage(imageBytes);
        userImage.setId(userId);

        when(userImageRepository.findById(userId)).thenReturn(Optional.of(userImage));

        byte[] result = userImageServiceImpl.getProfileByUserId(userId);

        assertArrayEquals(imageBytes, result);
    }

    @Test
    void getProfileByUserId_ThrowsResponseStatusException() {
        Long userId = 1L;

        when(userImageRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userImageServiceImpl.getProfileByUserId(userId));
    }

    @Test
    void getProfileByUserId_ThrowsUsernameNotFoundException() {
        Long userId = 1L;

        when(userImageRepository.findById(userId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResponseStatusException.class, () -> userImageServiceImpl.getProfileByUserId(userId));

        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void changeProfileImage_NullMultipartFile_ThrowsException() {
        Long userImageId = 1L;
        UserImage existingUserImage = new UserImage();
        existingUserImage.setId(userImageId);

        ImageUploadDto imageUploadDto = new ImageUploadDto();
        imageUploadDto.setId(userImageId);
        imageUploadDto.setImage(null);

        when(userImageRepository.findById(userImageId)).thenReturn(Optional.of(existingUserImage));

        assertThrows(NullPointerException.class, () -> userImageServiceImpl.changeProfileImage(imageUploadDto));
    }
}
