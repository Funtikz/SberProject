package org.example.sberproject.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.ServiceResponseDto;
import org.example.sberproject.entity.ResponseStatus;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.ServiceResponse;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.ResponseException;
import org.example.sberproject.exceptions.ServiceNotFoundException;
import org.example.sberproject.repository.ServiceResponseRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
@Slf4j
public class ServiceResponseService {
    private final ServiceResponseRepository repository;
    private final UserService userService;
    private final ServiceDealService dealService;
    private final JavaMailSender emailSender;
    private final ServiceDealService serviceDealService;
    private final EmailService emailService;


    @Transactional
    public void updateResponseStatus(Long serviceResponseId, ResponseStatus newStatus){
        ServiceResponse service = repository.findById(serviceResponseId).orElseThrow(() -> {
            log.error("Услуги с " + serviceResponseId + "не существует");
            throw new ServiceNotFoundException("Услуги с " + serviceResponseId + "не существует");
        });

        if (service.getResponseStatus() != ResponseStatus.PENDING) {
            throw new ResponseException("Этот отклик уже обработан!");
        }

        service.setResponseStatus(newStatus);
        repository.save(service);
        User consumer = service.getUser();
        User producer = service.getServiceDeal().getApplicant();
        if (newStatus.equals(ResponseStatus.ACCEPTED)){
            emailService.exchangeContactData(producer, consumer, service.getServiceDeal(), service.getOfferedServiceDeal());
            // TODO Добавить изменение категории услуги, чтобы она не показывалась в новом поиске
        }
        else {
            emailService.sendOfferRejectedNotification(consumer, service.getServiceDeal(), service.getOfferedServiceDeal());
        }
    }




    public List<ServiceResponseDto> getAllMyResponse(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User consumer = userService.findByPhoneNumber(login);
        return repository.findAllByUser(consumer).stream().map(this::mapToDto).toList();
    }

    public List<ServiceResponseDto> getResponsesForMyService(Long serviceDealId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User producer = userService.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceDealId);
        if (!serviceDeal.getApplicant().equals(producer)) {
            throw new ResponseException("Вы не являетесь владельцем этой услуги!");
        }
        List<ServiceResponse> responses = repository.findAllByServiceDeal(serviceDeal);
        return responses.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public void responseToService(Long serviceId, Long offeredServiceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User consumer = userService.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceId);
        ServiceDeal offeredServiceDeal = dealService.findById(offeredServiceId);
        User producer = serviceDeal.getApplicant();

        if (repository.findByServiceDealAndUser(serviceDeal, consumer).isPresent()){
            log.error("Вы уже откликались на данную заявку!");
            throw new ResponseException("Вы уже откликались на данную заявку!");
        }
        if (consumer.getId().equals(producer.getId())){
            log.error("Нельзя откликнуться на свою же заявку");
            throw new ResponseException("Нельзя откликнуться на свою же заявку");
        }


        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setDateOfResponse(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        serviceResponse.setServiceDeal(serviceDeal);
        serviceResponse.setOfferedServiceDeal(offeredServiceDeal);
        serviceResponse.setUser(consumer);
        repository.save(serviceResponse);
        emailService.sendEmailNotification(producer.getEmail(), consumer.getRating(),
                serviceDeal, offeredServiceDeal, consumer.getFirstName(), consumer.getLastName());
        log.info("Вы откликнулись на услугу с ID: " + serviceId);

    }

    private ServiceResponseDto mapToDto(ServiceResponse serviceResponse) {
        ServiceResponseDto dto = new ServiceResponseDto();
        dto.setId(serviceResponse.getId());
        dto.setRating(userService.toDto(serviceResponse.getUser().getRating()));
        dto.setServiceDeal(dealService.toNotAuthDto(serviceResponse.getServiceDeal()));
        dto.setDateOfResponse(serviceResponse.getDateOfResponse());
        dto.setResponseStatus(serviceResponse.getResponseStatus());
        dto.setOfferedServiceDeal(dealService.toNotAuthDto(serviceResponse.getOfferedServiceDeal()));
        return dto;
    }
}
