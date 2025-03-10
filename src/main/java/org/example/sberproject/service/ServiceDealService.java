package org.example.sberproject.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.dto.service.NotAuthServiceResponseDto;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.UsernameNotFoundException;
import org.example.sberproject.repository.ServiceDealRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ServiceDealService {

    private final ServiceDealRepository serviceRepository;
    private final UserService userService;

    public Page<?> getAllServices(Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated){
            return serviceRepository.findAll(pageable).map(this::toAuthDto);
        }
        return serviceRepository.findAll(pageable).map(this::toNotAuthDto);

    }

    public Page<?> getServicesByCategory(Category category, Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated){
            return serviceRepository.findByCategoryService(category, pageable)
                    .map(this::toAuthDto);
        }
        return serviceRepository.findByCategoryService(category, pageable).map(this::toNotAuthDto);
    }

    public Page<AuthServiceDealResponseDto> getMyServices(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User currentUser = userService.findByPhoneNumber(login);
        return serviceRepository.findByApplicant(currentUser, pageable)
                .map(this::toAuthDto);
    }

    @Transactional
    public AuthServiceDealResponseDto createService(ServiceDealRequestDto requestDto) {
        User user = userService.findById(requestDto.getUserId());
        ServiceDeal service = new ServiceDeal();
        service.setApplicant(user);
        service.setCategoryService(requestDto.getCategoryService());
        service.setDescriptionService(requestDto.getDescriptionService());
        service.setAwaitingCategoryService(requestDto.getAwaitingCategoryService());
        service.setAwaitCategoryDescription(requestDto.getAwaitCategoryDescription());
        service.setDateOfPublication(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        ServiceDeal save = serviceRepository.save(service);
        log.info("Заявка с пользователем " + user.getId() + " создана");
        return  toAuthDto(save);
    }

    @Transactional
    public AuthServiceDealResponseDto updateService(Long serviceId, ServiceDealRequestDto requestDto) {
        ServiceDeal service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.error("Услуга с ID {} не существует", serviceId);
                    return new UsernameNotFoundException("Услуга с таким ID не найдена");
                });
        User user = userService.findById(requestDto.getUserId());
        service.setApplicant(user);
        service.setCategoryService(requestDto.getCategoryService());
        service.setDescriptionService(requestDto.getDescriptionService());
        service.setAwaitingCategoryService(requestDto.getAwaitingCategoryService());
        service.setAwaitCategoryDescription(requestDto.getAwaitCategoryDescription());
        service.setDateOfPublication(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        ServiceDeal updatedService = serviceRepository.save(service);
        return toAuthDto(updatedService);
    }



    private AuthServiceDealResponseDto toAuthDto(ServiceDeal entity) {
        AuthServiceDealResponseDto dto = new AuthServiceDealResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryService(entity.getCategoryService());
        dto.setDescriptionService(entity.getDescriptionService());
        dto.setAwaitingCategoryService(entity.getAwaitingCategoryService());
        dto.setAwaitCategoryDescription(entity.getAwaitCategoryDescription());
        dto.setUserId(entity.getApplicant().getId());
        dto.setPhoneNumber(entity.getApplicant().getPhoneNumber());
        dto.setEmail(entity.getApplicant().getEmail());
        dto.setDateOfPublication(entity.getDateOfPublication());

        return dto;
    }


    private NotAuthServiceResponseDto toNotAuthDto(ServiceDeal entity) {
        NotAuthServiceResponseDto dto = new NotAuthServiceResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryService(entity.getCategoryService());
        dto.setDescriptionService(entity.getDescriptionService());
        dto.setAwaitingCategoryService(entity.getAwaitingCategoryService());
        dto.setAwaitCategoryDescription(entity.getAwaitCategoryDescription());
        dto.setDateOfPublication(entity.getDateOfPublication());

        return dto;
    }


}
