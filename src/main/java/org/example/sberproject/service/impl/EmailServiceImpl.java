package org.example.sberproject.service.impl;

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
    public void sendOfferRejectedNotification(User consumer, ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal){
        SimpleMailMessage messageConsumer = new SimpleMailMessage();
        messageConsumer.setTo(consumer.getEmail());
        messageConsumer.setSubject("Ваше предложение отклонили!" + serviceDeal.getDescriptionService());
        messageConsumer.setText("Здравствуйте! Ранее вы откликались на услугу " + serviceDeal.getDescriptionService() + "\n" +
                "Предлагали взамен: " + offeredServiceDeal.getDescriptionService()  + "\n" +
                "к сожалению ваше предложение отклонили!");
        emailSender.send(messageConsumer);
    }

    @Async
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
}