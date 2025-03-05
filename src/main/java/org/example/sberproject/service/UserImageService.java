package org.example.sberproject.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.user.ImageUploadDto;
import org.example.sberproject.entity.UserImage;
import org.example.sberproject.exceptions.FileIsEmpty;
import org.example.sberproject.exceptions.UserImageProfileNotFound;
import org.example.sberproject.exceptions.UsernameNotFoundException;
import org.example.sberproject.repository.UserImageRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

@Data
@RequiredArgsConstructor
@Slf4j
@Service
public class UserImageService {
    private final UserImageRepository repo;
    private final UserService userService;

    @Transactional
    public void changeProfileImage(ImageUploadDto imageUploadDto){
        UserImage userImageById = findUserImageById(imageUploadDto.getId());
        MultipartFile multipartFile = imageUploadDto.getImage();
        if (!multipartFile.isEmpty()){
            try {
                userImageById.setProfileImage(multipartFile.getBytes());
                repo.save(userImageById);
                log.info("Изображение профиля успешно обновлено!");
            } catch (IOException e) {
                log.error("Ошибка при чтении данных из файла!");
                throw new RuntimeException(e);
            }
        }
        else {
            log.error("Переданный файл пустой!");
            throw new FileIsEmpty();
        }
    }

    @Transactional
    public void deleteProfileImageAndUploadDefault(Long userId){
        UserImage userImageById = findUserImageById(userId);
        byte[] defaultImage = getDefaultProfileImageBytes();
        if (Arrays.equals(userImageById.getProfileImage(), defaultImage)){
            log.error("У вас нет фотографий");
            throw new UserImageProfileNotFound();
        }
        userImageById.setProfileImage(defaultImage);
        repo.save(userImageById);
    }

    public byte[] getProfileByUserId(Long userId){
        Optional<UserImage> userImage = repo.findById(userId);
        return userImage.map(UserImage::getProfileImage).orElseThrow(
                () ->
                {
                    String errorMessage = "Пользователь с id:  " +  userId +  "не найден";
                    log.error(errorMessage, new UsernameNotFoundException(errorMessage));
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");

                });
    }
    public UserImage findUserImageById (Long userImageId){
        return repo.findById(userImageId).orElseThrow(
                () ->{
                    String errorMessage = "Пользователь с id:  " +  userImageId  +  "не найден";
                    log.error(errorMessage, new UsernameNotFoundException(errorMessage));
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                }
        );
    }

    public byte[] getDefaultProfileImageBytes(){
        ClassPathResource resource = new ClassPathResource("images/default_profile.jpg");

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
