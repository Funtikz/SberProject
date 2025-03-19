package org.example.sberproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.dto.service.NotAuthServiceDealResponseDto;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.ServiceNotFoundException;
import org.example.sberproject.repository.ServiceDealRepository;
import org.example.sberproject.service.api.ServiceDealService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ServiceDealServiceImpl implements ServiceDealService {

    private final ServiceDealRepository serviceRepository;
    private final UserServiceImpl userServiceImpl;

    public Page<NotAuthServiceDealResponseDto> getAllServices(Pageable pageable){
        return serviceRepository.findAll(pageable).map(this::toNotAuthDto);

    }

    public Page<NotAuthServiceDealResponseDto> searchServicesByCategory(List<Category> category, Pageable pageable) {
        return serviceRepository.findByCategoryServiceIn(category, pageable).map(this::toNotAuthDto);
    }

    public Page<NotAuthServiceDealResponseDto> getMyServices(Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User currentUser = userServiceImpl.findByPhoneNumber(login);
        return serviceRepository.findByApplicant(currentUser, pageable)
                .map(this::toNotAuthDto);
    }


    public Page<NotAuthServiceDealResponseDto> searchServicesByKeyword(Pageable pageable, String message){
        return serviceRepository.findByDescriptionServiceContainingIgnoreCase(message, pageable)
                .map(this::toNotAuthDto);
    }

    @Transactional
    public AuthServiceDealResponseDto createService(ServiceDealRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userServiceImpl.findByPhoneNumber(login);
        ServiceDeal service = new ServiceDeal();
        service.setApplicant(user);
        service.setCategoryService(requestDto.getCategoryService());
        service.setDescriptionService(requestDto.getDescriptionService());
        service.setAwaitingCategoryService(requestDto.getAwaitingCategoryService());
        service.setAwaitDescriptionService(requestDto.getAwaitDescriptionService());
        service.setDateOfPublication(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        ServiceDeal save = serviceRepository.save(service);
        log.info("Заявка с пользователем " + user.getId() + " создана");
        return  toAuthDto(save);
    }

    @Transactional
    public AuthServiceDealResponseDto updateService(Long serviceId, ServiceDealRequestDto requestDto) {
        ServiceDeal service = findById(serviceId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userServiceImpl.findByPhoneNumber(login);
        service.setApplicant(user);
        service.setCategoryService(requestDto.getCategoryService());
        service.setDescriptionService(requestDto.getDescriptionService());
        service.setAwaitingCategoryService(requestDto.getAwaitingCategoryService());
        service.setAwaitDescriptionService(requestDto.getAwaitDescriptionService());
        service.setDateOfPublication(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        ServiceDeal updatedService = serviceRepository.save(service);
        return toAuthDto(updatedService);
    }

    @Transactional
    public void deleteService(Long serviceId){
        ServiceDeal serviceDeal = serviceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.error("Услуга с ID {} не существует", serviceId);
                    return new ServiceNotFoundException("Услуги с ID" + serviceId + "не существует");
                });
        serviceRepository.delete(serviceDeal);
    }

    public ServiceDeal findById(Long serviceId){
        return serviceRepository.findById(serviceId)
                .orElseThrow(() -> {
                    log.error("Услуга с ID {} не существует", serviceId);
                    return new ServiceNotFoundException("Услуги с ID" + serviceId + "не существует");
                });
    }


    public AuthServiceDealResponseDto toAuthDto(ServiceDeal entity) {
        AuthServiceDealResponseDto dto = new AuthServiceDealResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryService(entity.getCategoryService());
        dto.setDescriptionService(entity.getDescriptionService());
        dto.setAwaitingCategoryService(entity.getAwaitingCategoryService());
        dto.setAwaitDescriptionService(entity.getAwaitDescriptionService());
        dto.setUserId(entity.getApplicant().getId());
        dto.setPhoneNumber(entity.getApplicant().getPhoneNumber());
        dto.setEmail(entity.getApplicant().getEmail());
        dto.setDateOfPublication(entity.getDateOfPublication());

        return dto;
    }


    public NotAuthServiceDealResponseDto toNotAuthDto(ServiceDeal entity) {
        NotAuthServiceDealResponseDto dto = new NotAuthServiceDealResponseDto();
        dto.setId(entity.getId());
        dto.setCategoryService(entity.getCategoryService());
        dto.setDescriptionService(entity.getDescriptionService());
        dto.setAwaitingCategoryService(entity.getAwaitingCategoryService());
        dto.setAwaitDescriptionService(entity.getAwaitDescriptionService());
        dto.setDateOfPublication(entity.getDateOfPublication());

        return dto;
    }


}
