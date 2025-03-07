package org.example.sberproject.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.user.UserAuthDto;
import org.example.sberproject.dto.user.UserDto;
import org.example.sberproject.dto.user.UserRegistrationDto;
import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserImage;
import org.example.sberproject.exceptions.*;
import org.example.sberproject.repository.UserImageRepository;
import org.example.sberproject.repository.UserRepository;
import org.example.sberproject.security.jwt.JwtService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserImageRepository userImageRepository;


    //Вход для User и установка в куки
    public UserDto login(UserAuthDto userCredentialDto, HttpServletResponse response){
        // 1. Ищем пользователя по номеру телефона
        log.info("Поиск пользователя по номеру телефона");
        String number = userCredentialDto.getPhoneNumber();
        String password = userCredentialDto.getPassword();
        Optional<User> userOpt = userRepository.findByPhoneNumber(number);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            log.debug("Пользователь с телефоном" + user.getPhoneNumber() + "найден");
            if (passwordEncoder.matches(password, user.getPassword())) {
                // 3. Генерируем JWT с ролями
                log.info("Пароли совпадают: генерируем JWT");
                String jwtToken = jwtService.generateJwtToken(user.getPhoneNumber(), user.getRoles());

                // 4. Создаем httpOnly cookie
                Cookie jwtCookie = new Cookie("jwt-auth-token", jwtToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(180 * 24 * 60 * 60); // Время жизни куки - 180 дней

                // 5. Добавляем куки в response
                response.addCookie(jwtCookie);
                UserDto dto = toDto(user);
                log.info("JWT добавлена в куки");
                return dto;
            }else {
                log.warn("Неверный пароль для пользователя {}", number);
                throw new DifferentPassword();
            }
        }
        // 6. Если аутентификация неуспешна, возвращаем ошибку
        log.error("Пользователь с номером телефона: {} не найден", number);
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
    }

    //Регистрация нового пользователя через номер телефона или почту
    @Transactional
    public UserRegistrationDto addUser(UserRegistrationDto userDto) {
        String phoneNumber = userDto.getPhoneNumber();
        if (userRepository.findByPhoneNumber(userDto.getPhoneNumber()).isPresent()) {
            log.error("Пользователь с номером телефона: {} зарегистрирован!", phoneNumber);
            throw new UserAlreadyExistsException("Пользователь с номером телефона:" + phoneNumber + "зарегистрирован!");
        }
        User entity = RegistrationDtotoUser(userDto);
        if (entity == null) {
            String errorMessage = "Не получилось преобразовать RegistrationDto в User";
            log.error(errorMessage);
            throw new MappingException(errorMessage);
        }
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())){
            log.error("Пароли не совпадают!");
            throw new DifferentPassword();
        }
        entity.setRoles(userDto.getRoles());
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User currentUser = userRepository.save(entity);
        setDefaultImage(currentUser);

        return toUserRegistrationDto(currentUser);
    }

    public byte[] getDefaultProfileImageBytes(){
        ClassPathResource resource = new ClassPathResource("images/default_profile.jpg");

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Transactional
    public void setDefaultImage(User user){
        //Сохранение в БД изображения пользователя по дефолту
        UserImage userImage = new UserImage();
        userImage.setProfileImage(getDefaultProfileImageBytes());
        userImage.setUser(user);
        userImageRepository.save(userImage);
        log.info("Дефолтная картинка пользователю установлена");
    }

    public UserDto findByPhoneNumber(String number){
        User user = userRepository.findByPhoneNumber(number).orElseThrow(
                () ->{
                    String errorMessage = "Пользователь с номером телефона:  " + number + "не найден";
                    log.error(errorMessage, new UsernameNotFoundException(errorMessage));
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
                }
        );
        return toDto(user);
    }

    public User getCurrentUser(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            String phoneNumber = userDetails.getUsername();
            return toUser(findByPhoneNumber(phoneNumber));
        }
        else {
            log.error("Пользователь не авторизован не найден JWT токен");
            throw new AuthenticationException("Пользователь не авторизован");
        }
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    @Transactional
    public UserDto updateUser(UserDto userDto){
        return toDto(userRepository.save(toUser(userDto)));
    }



    public User RegistrationDtotoUser(UserRegistrationDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(userDto.getRoles());
        user.setId(userDto.getId());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        return  user;
    }

    public UserRegistrationDto toUserRegistrationDto(User user){
        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(user.getRoles());
        userDto.setId(user.getId());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setId(user.getId());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserDto toDto(User user){
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setRoles(user.getRoles());
        userDto.setId(user.getId());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setId(user.getId());
        userDto.setPassword(user.getPassword());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User toUser(UserDto userDto){
        User user = new User();
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setRoles(userDto.getRoles());
        user.setId(userDto.getId());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setPassword(userDto.getPassword());
        user.setEmail(userDto.getEmail());
        return  user;
    }


}
