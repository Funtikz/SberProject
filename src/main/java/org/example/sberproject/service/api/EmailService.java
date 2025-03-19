package org.example.sberproject.service.api;

import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserRating;

/**
 * Сервис для отправки email-уведомлений пользователям.
 */
public interface EmailService {

    /**
     * Отправляет email-уведомление пользователю о сделке и рейтинге.
     *
     * @param recipientEmail     Email-адрес получателя.
     * @param rating             Рейтинг пользователя.
     * @param serviceDeal        Сделка, которую предложил пользователь.
     * @param offeredServiceDeal Сделка, на которую было сделано предложение.
     * @param firstName          Имя пользователя.
     * @param lastName           Фамилия пользователя.
     */
    void sendEmailNotification(String recipientEmail, UserRating rating,
                               ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal,
                               String firstName, String lastName);

    /**
     * Отправляет email-уведомление о том, что предложение пользователя было отклонено.
     *
     * @param consumer           Пользователь, которому отправляется уведомление.
     * @param serviceDeal        Сделка, которую предложил пользователь.
     * @param offeredServiceDeal Сделка, на которую было сделано предложение.
     */
    void sendOfferRejectedNotification(User consumer, ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal);

    /**
     * Обменивается контактными данными между пользователями после успешной сделки.
     *
     * @param producer           Пользователь, предоставляющий услугу.
     * @param consumer           Пользователь, получающий услугу.
     * @param serviceDeal        Сделка, которую предложил producer.
     * @param offeredServiceDeal Сделка, на которую было сделано предложение.
     */
    void exchangeContactData(User producer, User consumer,
                             ServiceDeal serviceDeal, ServiceDeal offeredServiceDeal);
}
