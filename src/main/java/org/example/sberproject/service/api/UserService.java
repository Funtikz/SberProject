package org.example.sberproject.service.api;

import jakarta.servlet.http.HttpServletResponse;
import org.example.sberproject.dto.user.*;
import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserRating;

import java.util.List;

/**
 * Сервис для управления пользователями, включая регистрацию, аутентификацию, обновление информации и получение данных.
 */
public interface UserService {

    /**
     * Выполняет аутентификацию пользователя и устанавливает JWT токен в куки.
     *
     * @param userCredentialDto Объект с данными для аутентификации (номер телефона и пароль).
     * @param response HTTP ответ, в который будет добавлен JWT токен в виде cookie.
     * @return {@link UserResponseDto} - информация о пользователе.
     * @throws org.springframework.web.server.ResponseStatusException Если аутентификация не удалась.
     */
    UserResponseDto login(UserAuthDto userCredentialDto, HttpServletResponse response);

    /**
     * Регистрирует нового пользователя.
     *
     * @param userDto Объект с данными для регистрации нового пользователя.
     * @return {@link UserRegistrationDto} - информация о зарегистрированном пользователе.
     * @throws org.example.sberproject.exceptions.UserAlreadyExistsException Если пользователь с таким номером уже существует.
     * @throws org.example.sberproject.exceptions.DifferentPassword Если введенные пароли не совпадают.
     * @throws org.example.sberproject.exceptions.MappingException Если произошла ошибка при преобразовании DTO в сущность.
     */
    UserRegistrationDto addUser(UserRegistrationDto userDto);

    /**
     * Получает изображение профиля по умолчанию.
     *
     * @return Массив байтов, представляющий изображение профиля по умолчанию.
     */
    byte[] getDefaultProfileImageBytes();

    /**
     * Устанавливает изображение профиля по умолчанию для пользователя.
     *
     * @param user Объект пользователя, которому нужно установить дефолтное изображение.
     */
    void setDefaultImage(User user);

    /**
     * Находит пользователя по номеру телефона.
     *
     * @param number Номер телефона пользователя.
     * @return {@link User} - найденный пользователь.
     * @throws org.example.sberproject.exceptions.UsernameNotFoundException Если пользователь с таким номером не найден.
     */
    User findByPhoneNumber(String number);

    /**
     * Получает текущего авторизованного пользователя.
     *
     * @param principal Объект, содержащий информацию о текущем пользователе.
     * @return {@link User} - текущий пользователь.
     * @throws org.example.sberproject.exceptions.AuthenticationException Если пользователь не авторизован.
     */
    User getCurrentUser(Object principal);

    /**
     * Получает всех пользователей.
     *
     * @return Список всех пользователей.
     */
    List<User> getAll();

    /**
     * Обновляет информацию о пользователе.
     *
     * @param userResponseDto Объект с обновленной информацией о пользователе.
     * @return {@link UserResponseDto} - информация об обновленном пользователе.
     */
    UserResponseDto updateUser(UserResponseDto userResponseDto);

    /**
     * Находит пользователя по ID.
     *
     * @param userId ID пользователя.
     * @return {@link User} - найденный пользователь.
     * @throws org.springframework.web.server.ResponseStatusException Если пользователь не найден.
     */
    User findById(Long userId);

    /**
     * Преобразует объект {@link UserRegistrationDto} в сущность {@link User}.
     *
     * @param userDto Объект DTO с данными для регистрации.
     * @return {@link User} - сущность пользователя.
     */
    User RegistrationDtotoUser(UserRegistrationDto userDto);

    /**
     * Преобразует сущность {@link User} в объект {@link UserRegistrationDto}.
     *
     * @param user Объект {@link User}.
     * @return {@link UserRegistrationDto} - DTO пользователя.
     */
    UserRegistrationDto toUserRegistrationDto(User user);

    /**
     * Преобразует сущность {@link User} в объект {@link UserResponseDto}.
     *
     * @param user Объект {@link User}.
     * @return {@link UserResponseDto} - DTO пользователя.
     */
    UserResponseDto toDto(User user);

    /**
     * Преобразует объект {@link UserResponseDto} в сущность {@link User}.
     *
     * @param userResponseDto Объект {@link UserResponseDto}.
     * @return {@link User} - сущность пользователя.
     */
    User toUser(UserResponseDto userResponseDto);

    /**
     * Преобразует сущность {@link UserRating} в объект {@link UserRatingDto}.
     *
     * @param entity Объект {@link UserRating}.
     * @return {@link UserRatingDto} - DTO рейтинга пользователя.
     */
    UserRatingDto toDto(UserRating entity);
}
