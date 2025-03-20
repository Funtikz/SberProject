package org.example.sberproject.service;

import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.entity.UserRating;
import org.example.sberproject.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender emailSender;

    @InjectMocks
    private EmailServiceImpl emailServiceImpl;

    private User user;
    private UserRating userRating;
    private ServiceDeal serviceDeal;
    private ServiceDeal offeredServiceDeal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123456789");

        userRating = new UserRating();
        userRating.setRating(5.0);
        userRating.setSuccessfulExchanges(10);
        userRating.setUser(user);

        serviceDeal = new ServiceDeal();
        serviceDeal.setDescriptionService("Service A");
        serviceDeal.setCategoryService(Category.IT);

        offeredServiceDeal = new ServiceDeal();
        offeredServiceDeal.setDescriptionService("Offered Service B");
        offeredServiceDeal.setCategoryService(Category.DESIGN);
    }

    @Test
    void testSendEmailNotificationValid() {
        emailServiceImpl.sendEmailNotification(user.getEmail(), userRating, serviceDeal, offeredServiceDeal, user.getFirstName(), user.getLastName());
        verify(emailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
    }

    @Test
    void testSendEmailNotificationEmptyEmail() {
        String invalidEmail = "";

        emailServiceImpl.sendEmailNotification(invalidEmail, userRating, serviceDeal, offeredServiceDeal, user.getFirstName(), user.getLastName());

        verify(emailSender, times(0)).send(Mockito.any(SimpleMailMessage.class));
    }

//    @Test
//    void testSendOfferRejectedNotification() {
//        emailServiceImpl.sendOfferRejectedNotification(user, serviceDeal, offeredServiceDeal);
//        verify(emailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
//    }

//    @Test
//    void testExchangeContactData() {
//        emailServiceImpl.exchangeContactData(user, user, serviceDeal, offeredServiceDeal);
//        verify(emailSender, times(2)).send(Mockito.any(SimpleMailMessage.class));
//    }
}
