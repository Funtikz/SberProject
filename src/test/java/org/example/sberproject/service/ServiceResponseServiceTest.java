package org.example.sberproject.service;

import org.example.sberproject.dto.service.ServiceResponseDto;
import org.example.sberproject.entity.*;
import org.example.sberproject.exceptions.ResponseException;
import org.example.sberproject.repository.ServiceResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceResponseServiceTest {

    @Mock
    private ServiceResponseRepository repository;

    @Mock
    private UserService userService;

    @Mock
    private ServiceDealService dealService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ServiceResponseService serviceResponseService;

    private User consumer;
    private User producer;
    private ServiceDeal serviceDeal;
    private ServiceDeal offeredServiceDeal;
    private ServiceResponse serviceResponse;

    @BeforeEach
    void setUp() {
        consumer = new User();
        consumer.setId(1L);
        consumer.setPhoneNumber("+79112223344");

        producer = new User();
        producer.setId(2L);
        producer.setPhoneNumber("+79223334455");

        serviceDeal = new ServiceDeal();
        serviceDeal.setId(100L);
        serviceDeal.setApplicant(producer);

        offeredServiceDeal = new ServiceDeal();
        offeredServiceDeal.setId(200L);
        offeredServiceDeal.setApplicant(consumer);

        serviceResponse = new ServiceResponse();
        serviceResponse.setId(1L);
        serviceResponse.setUser(consumer);
        serviceResponse.setServiceDeal(serviceDeal);
        serviceResponse.setDateOfResponse(LocalDateTime.now());
        serviceResponse.setResponseStatus(ResponseStatus.PENDING);
    }

    private void mockAuthentication(String phoneNumber) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(phoneNumber);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void updateResponseStatus_Success() {
        when(repository.findById(1L)).thenReturn(Optional.of(serviceResponse));

        serviceResponseService.updateResponseStatus(1L, ResponseStatus.ACCEPTED);

        assertEquals(ResponseStatus.ACCEPTED, serviceResponse.getResponseStatus());
        verify(repository, times(1)).save(serviceResponse);
    }

    @Test
    void updateResponseStatus_FailsIfAlreadyProcessed() {
        serviceResponse.setResponseStatus(ResponseStatus.ACCEPTED);
        when(repository.findById(1L)).thenReturn(Optional.of(serviceResponse));

        ResponseException exception = assertThrows(ResponseException.class, () ->
                serviceResponseService.updateResponseStatus(1L, ResponseStatus.REJECTED));

        assertEquals("Этот отклик уже обработан!", exception.getMessage());
    }

    @Test
    void getAllMyResponse_Success() {
        mockAuthentication("+79112223344");

        when(userService.findByPhoneNumber("+79112223344")).thenReturn(consumer);
        when(repository.findAllByUser(consumer)).thenReturn(List.of(serviceResponse));

        List<ServiceResponseDto> responses = serviceResponseService.getAllMyResponse();

        assertEquals(1, responses.size());
    }

    @Test
    void getResponsesForMyService_Success() {
        mockAuthentication("+79223334455");

        when(userService.findByPhoneNumber("+79223334455")).thenReturn(producer);
        when(dealService.findById(100L)).thenReturn(serviceDeal);
        when(repository.findAllByServiceDeal(serviceDeal)).thenReturn(List.of(serviceResponse));

        List<ServiceResponseDto> responses = serviceResponseService.getResponsesForMyService(100L);

        assertEquals(1, responses.size());
    }

    @Test
    void responseToService_Success() {
        mockAuthentication("+79112223344");

        when(userService.findByPhoneNumber("+79112223344")).thenReturn(consumer);
        when(dealService.findById(100L)).thenReturn(serviceDeal);
        when(dealService.findById(200L)).thenReturn(offeredServiceDeal);
        when(repository.findByServiceDealAndUser(serviceDeal, consumer)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> serviceResponseService.responseToService(100L, 200L));

        verify(repository, times(1)).save(any(ServiceResponse.class));
        verify(emailService, times(1)).sendEmailNotification(
                eq(producer.getEmail()), any(), eq(serviceDeal), eq(offeredServiceDeal),
                eq(consumer.getFirstName()), eq(consumer.getLastName())
        );
    }

    @Test
    void responseToService_Fail_SelfResponse() {
        mockAuthentication("+79112223344");

        serviceDeal.setApplicant(consumer);

        when(userService.findByPhoneNumber("+79112223344")).thenReturn(consumer);
        when(dealService.findById(100L)).thenReturn(serviceDeal);

        ResponseException exception = assertThrows(ResponseException.class,
                () -> serviceResponseService.responseToService(100L, 200L));

        assertEquals("Нельзя откликнуться на свою же заявку", exception.getMessage());

        verify(repository, never()).save(any(ServiceResponse.class));
    }

    @Test
    void responseToService_Fail_AlreadyResponded() {
        mockAuthentication("+79112223344");

        when(userService.findByPhoneNumber("+79112223344")).thenReturn(consumer);
        when(dealService.findById(100L)).thenReturn(serviceDeal);
        when(repository.findByServiceDealAndUser(serviceDeal, consumer)).thenReturn(Optional.of(serviceResponse));

        ResponseException exception = assertThrows(ResponseException.class,
                () -> serviceResponseService.responseToService(100L, 200L));

        assertEquals("Вы уже откликались на данную заявку!", exception.getMessage());

        verify(repository, never()).save(any(ServiceResponse.class));
    }
}
