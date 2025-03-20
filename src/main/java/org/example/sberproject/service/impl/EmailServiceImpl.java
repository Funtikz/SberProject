package org.example.sberproject.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserRating;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl {

    private final JavaMailSender emailSender;

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @Async
    public void sendEmailNotification(String recipientEmail, UserRating rating,
                                      ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal, String firstName,
                                      String lastName) {
        if (recipientEmail == null || recipientEmail.isEmpty()) {
            log.warn("У пользователя отсутствует email. Уведомление не отправлено.");
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Новый отклик на вашу услугу!");
        message.setText("Здравствуйте! На вашу услугу ' " + serviceDeal.getDescriptionService() + " ' откликнулся пользователь.\n" +
                "Фамилия: " + lastName + "\n" +
                "Имя: " + firstName + "\n" +
                "Его предложение взамен: " + "\n" +
                "Категория услуги: " + offeredServiceDeal.getCategoryService() + "\n" +
                "Описание: " + offeredServiceDeal.getDescriptionService() + "\n" +
                "Пожалуйста примите или отклоните данную заявку в приложении!");

        emailSender.send(message);
    }

    @Async
    public void sendOfferRejectedNotification(String consumerEmail, String serviceDealDescription, String offeredServiceDescription) {
        SimpleMailMessage messageConsumer = new SimpleMailMessage();
        messageConsumer.setTo(consumerEmail);
        messageConsumer.setSubject("Ваше предложение отклонили! " + serviceDealDescription);
        messageConsumer.setText("Здравствуйте! Ранее вы откликались на услугу " + serviceDealDescription + "\n" +
                "Предлагали взамен: " + offeredServiceDescription + "\n" +
                "К сожалению, ваше предложение отклонили!");
        emailSender.send(messageConsumer);
    }

    @Async
    public void exchangeContactData(String producerFirstName, String producerLastName, String producerEmail, String producerPhone,
                                    String consumerFirstName, String consumerLastName, String consumerEmail, String consumerPhone,
                                    String serviceDealDescription, String offeredServiceDescription) {
        SimpleMailMessage messageConsumer = new SimpleMailMessage();
        messageConsumer.setTo(consumerEmail);
        messageConsumer.setSubject("Ваше предложение приняли! " + serviceDealDescription);
        messageConsumer.setText("Здравствуйте! Ранее вы откликались на услугу " + serviceDealDescription + "\n" +
                "Предлагали взамен: " + offeredServiceDescription + "\n" +
                "ваше предложение приняли! Контактные данные для связи: " + "\n" +
                "Фамилия: " + producerLastName + "\n" +
                "Имя: " + producerFirstName + "\n" +
                "Почта: " + producerEmail + "\n" +
                "Номер телефона: " + producerPhone + "\n"
                + "Создатель услуги так же получил ваши контактные данные!");
        emailSender.send(messageConsumer);

        SimpleMailMessage messageProducer = new SimpleMailMessage();
        messageProducer.setTo(producerEmail);
        messageProducer.setSubject("Вы приняли сделку " + offeredServiceDescription);
        messageProducer.setText("Здравствуйте! Ранее вы создавали услугу " + serviceDealDescription + "\n" +
                "Вы приняли следующее предложение: " + offeredServiceDescription + "\n" +
                "Контактные данные для связи: " + "\n" +
                "Фамилия: " + consumerLastName + "\n" +
                "Имя: " + consumerFirstName + "\n" +
                "Почта: " + consumerEmail + "\n" +
                "Номер телефона: " + consumerPhone + "\n"
                + "Откликнувшийся участник так же получил ваши контактные данные!");
        emailSender.send(messageProducer);
    }

}