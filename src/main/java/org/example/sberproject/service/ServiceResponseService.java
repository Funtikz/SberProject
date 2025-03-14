package org.example.sberproject.service;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.ServiceResponseDto;
import org.example.sberproject.entity.*;
import org.example.sberproject.exceptions.ResponseException;
import org.example.sberproject.exceptions.ServiceNotFoundException;
import org.example.sberproject.repository.ServiceResponseRepository;
import org.springframework.mail.SimpleMailMessage;
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
            exchangeContactData(producer, consumer, service.getServiceDeal(), service.getOfferedServiceDeal());
            // TODO Добавить изменение категории услуги, чтобы она не показывалась в новом поиске
        }
        else {
            sendOfferRejectedNotification(consumer, service.getServiceDeal(), service.getOfferedServiceDeal());
        }


    }


    public void sendOfferRejectedNotification(User consumer, ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal){
        SimpleMailMessage messageConsumer = new SimpleMailMessage();
        messageConsumer.setTo(consumer.getEmail());
        messageConsumer.setSubject("Ваше предложение отклонили!" + serviceDeal.getDescriptionService());
        messageConsumer.setText("Здравствуйте! Ранее вы откликались на услугу " + serviceDeal.getDescriptionService() + "\n" +
                "Предлагали взамен: " + offeredServiceDeal.getDescriptionService()  + "\n" +
                "к сожалению ваше предложение отклонили!");
        emailSender.send(messageConsumer);
    }

    public void exchangeContactData(User producer, User consumer,
                                    ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal ){
        SimpleMailMessage messageConsumer = new SimpleMailMessage();
        messageConsumer.setTo(consumer.getEmail());
        messageConsumer.setSubject("Ваше предложение приняли!" + serviceDeal.getDescriptionService());
        messageConsumer.setText("Здравствуйте! Ранее вы откликались на услугу " + serviceDeal.getDescriptionService() + "\n" +
                "Предлагали взамен: " + offeredServiceDeal.getDescriptionService()  + "\n" +
                "ваше предложение приняли! Контактные данные для связи: "  + "\n" +
                "Фамилия: " + producer.getLastName() + "\n" +
                "Имя: " + producer.getFirstName() + "\n" +
                "Почта: " + producer.getEmail() + "\n" +
                "Номер телефона: " + producer.getPhoneNumber() + "\n"
                + "Создатель услуги так же получил ваши контактные данные!");
        emailSender.send(messageConsumer);

        SimpleMailMessage messageProducer = new SimpleMailMessage();
        messageProducer.setTo(producer.getEmail());
        messageProducer.setSubject("Вы приняли сделку " + offeredServiceDeal.getDescriptionService() );
        messageProducer.setText("Здравствуйте! Ранее вы создавали услугу " + serviceDeal.getDescriptionService()+ "\n" +
                "Вы приняли следующее предложение: " + offeredServiceDeal.getDescriptionService() + "\n" +
                "Контактные данные для связи: " + "\n" +
                "Фамилия: " + consumer.getLastName() + "\n" +
                "Имя: " + consumer.getFirstName() + "\n" +
                "Почта: " + consumer.getEmail() + "\n" +
                "Номер телефона: " + consumer.getPhoneNumber() + "\n"
                + "Откликнувшийся участник так же получил ваши контактные данные!");
        emailSender.send(messageProducer);
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
        sendEmailNotification(producer.getEmail(), consumer.getRating(),
                serviceDeal, offeredServiceDeal, consumer.getFirstName(), consumer.getLastName());
        log.info("Вы откликнулись на услугу с ID: " + serviceId);

    }


    private void sendEmailNotification(String recipientEmail, UserRating rating,
                                       ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal , String firstName,
                                       String lastName) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            log.warn("У пользователя отсутствует email. Уведомление не отправлено.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Новый отклик на вашу услугу!");
        message.setText("Здравствуйте! На вашу услугу ' " + serviceDeal.getDescriptionService() + " '  откликнулся пользователь.\n" +
                "Фамилия: " + lastName + "\n" +
                "Имя: " + firstName + "\n" +
                "Рейтинг: " + rating.getRating() + "\n" +
                "Количество успешных обменов: " + rating.getRating() + "\n" +
                "Его предложение взамен: " + "\n" +
                "Категория услуги: " + offeredServiceDeal.getCategoryService()  + "\n" +
                "Описание: " + offeredServiceDeal.getDescriptionService() + "\n" +
                "Пожалуйста примите или отклоните данную заявку в приложении!");

        emailSender.send(message);
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
