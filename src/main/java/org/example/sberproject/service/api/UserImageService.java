package org.example.sberproject.service.api;

import org.example.sberproject.dto.user.ImageUploadDto;
import org.example.sberproject.entity.UserImage;

/**
 * Сервис для управления изображениями профилей пользователей.
 */
public interface UserImageService {

    /**
     * Изменяет изображение профиля пользователя.
     *
     * @param imageUploadDto Объект, содержащий данные для загрузки нового изображения профиля,
     *                       включая ID пользователя и сам файл изображения.
     * @throws org.example.sberproject.exceptions.FileIsEmpty Если переданный файл пустой.
     * @throws org.springframework.web.server.ResponseStatusException Если возникла ошибка при обновлении изображения.
     */
    void changeProfileImage(ImageUploadDto imageUploadDto) throws org.example.sberproject.exceptions.FileIsEmpty, org.springframework.web.server.ResponseStatusException;

    /**
     * Удаляет изображение профиля пользователя и загружает изображение по умолчанию.
     *
     * @param userId ID пользователя, для которого нужно удалить изображение и установить изображение по умолчанию.
     * @throws org.example.sberproject.exceptions.UserImageProfileNotFound Если у пользователя нет изображения для удаления.
     */
    void deleteProfileImageAndUploadDefault(Long userId) throws org.example.sberproject.exceptions.UserImageProfileNotFound;

    /**
     * Получает изображение профиля пользователя по его ID.
     *
     * @param userId ID пользователя.
     * @return Массив байтов, представляющий изображение профиля пользователя.
     * @throws org.springframework.web.server.ResponseStatusException Если пользователь не найден.
     */
    byte[] getProfileByUserId(Long userId) throws org.springframework.web.server.ResponseStatusException;

    /**
     * Находит изображение профиля пользователя по ID изображения.
     *
     * @param userImageId ID изображения профиля.
     * @return Объект {@link UserImage}, представляющий изображение профиля пользователя.
     * @throws org.springframework.web.server.ResponseStatusException Если изображение не найдено.
     */
    UserImage findUserImageById(Long userImageId) throws org.springframework.web.server.ResponseStatusException;

    /**
     * Получает изображение профиля по умолчанию в виде массива байтов.
     *
     * @return Массив байтов, представляющий изображение профиля по умолчанию.
     * @throws java.io.IOException Если произошла ошибка при чтении изображения.
     */
    byte[] getDefaultProfileImageBytes() throws java.io.IOException;
}
