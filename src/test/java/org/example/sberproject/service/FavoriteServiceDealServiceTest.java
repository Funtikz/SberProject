package org.example.sberproject.service;

import org.example.sberproject.entity.FavoriteServiceDeal;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.FavoriteException;
import org.example.sberproject.repository.FavoriteServiceDealRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class FavoriteServiceDealServiceTest {

    @Autowired
    FavoriteServiceDealService favoriteService;

    @MockitoBean
    FavoriteServiceDealRepository repository;

    @MockitoBean
    UserService userService;

    @MockitoBean
    ServiceDealService dealService;

    @MockitoBean
    SecurityContext securityContext;

    @MockitoBean
    Authentication authentication;

    @AfterEach
    public void afterEach(){
        Mockito.reset(repository);
        Mockito.reset(userService);
        Mockito.reset(dealService);
        Mockito.reset(securityContext);
        Mockito.reset(authentication);

    }

    @Test
    void addToFavorite_Successful() {
        Long userId = 1L;
        String phoneNumber = "+79882505362";
        Long serviceId = 1L;

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(serviceId);
        serviceDeal.setApplicant(user);

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);
        when(dealService.findById(serviceId)).thenReturn(serviceDeal);

        FavoriteServiceDeal favoriteServiceDealMock = new FavoriteServiceDeal();
        favoriteServiceDealMock.setUser(user);
        favoriteServiceDealMock.setServiceDeal(serviceDeal);

        when(repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal)).thenReturn(Optional.empty());
        when(repository.save(any(FavoriteServiceDeal.class))).thenReturn(favoriteServiceDealMock);

        FavoriteServiceDeal favoriteServiceDeal = favoriteService.addToFavorite(serviceId);

        assertNotNull(favoriteServiceDeal);
        assertEquals(userId, favoriteServiceDeal.getUser().getId());
        assertEquals(phoneNumber, favoriteServiceDeal.getUser().getPhoneNumber());
        assertEquals(serviceDeal, favoriteServiceDeal.getServiceDeal());
    }

    @Test
    void addToFavorite_ThrowFavoriteException(){
        Long userId = 1L;
        String phoneNumber = "+79882505362";
        Long serviceId = 1L;

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(serviceId);
        serviceDeal.setApplicant(user);

        FavoriteServiceDeal favoriteServiceDeal = new FavoriteServiceDeal();
        favoriteServiceDeal.setId(1L);

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);
        when(dealService.findById(serviceId)).thenReturn(serviceDeal);

        FavoriteServiceDeal favoriteServiceDealMock = new FavoriteServiceDeal();
        favoriteServiceDealMock.setUser(user);
        favoriteServiceDealMock.setServiceDeal(serviceDeal);


        when(repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal))
                .thenReturn(Optional.of(favoriteServiceDeal));

        assertThrows(FavoriteException.class, () -> {
           favoriteService.addToFavorite(1L);
        });

    }

    @Test
    void deleteFromFavorites_Successful() {
        Long serviceId = 1L;
        String phoneNumber = "+79882505362";
        Long userId = 1L;

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(serviceId);
        serviceDeal.setApplicant(user);

        FavoriteServiceDeal favoriteServiceDeal = new FavoriteServiceDeal();
        favoriteServiceDeal.setUser(user);
        favoriteServiceDeal.setServiceDeal(serviceDeal);

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);
        when(dealService.findById(serviceId)).thenReturn(serviceDeal);

        when(repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal))
                .thenReturn(Optional.of(favoriteServiceDeal));

        favoriteService.deleteFromFavorites(serviceId);

        verify(repository, times(1)).delete(favoriteServiceDeal);
    }

    @Test
    void deleteFromFavorites_ServiceNotInFavorites() {
        Long serviceId = 1L;
        String phoneNumber = "+79882505362";
        Long userId = 1L;

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(serviceId);
        serviceDeal.setApplicant(user);

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);
        when(dealService.findById(serviceId)).thenReturn(serviceDeal);

        when(repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> favoriteService.deleteFromFavorites(serviceId));

        verify(repository, times(0)).delete(any(FavoriteServiceDeal.class));
    }

    @Test
    void findFavoriteServices_Successful() {
        Long userId = 1L;
        String phoneNumber = "+79882505362";
        Long serviceId1 = 1L;
        Long serviceId2 = 2L;

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        ServiceDeal serviceDeal1 = new ServiceDeal();
        serviceDeal1.setId(serviceId1);
        serviceDeal1.setApplicant(user);

        ServiceDeal serviceDeal2 = new ServiceDeal();
        serviceDeal2.setId(serviceId2);
        serviceDeal2.setApplicant(user);

        FavoriteServiceDeal favoriteServiceDeal1 = new FavoriteServiceDeal();
        favoriteServiceDeal1.setUser(user);
        favoriteServiceDeal1.setServiceDeal(serviceDeal1);

        FavoriteServiceDeal favoriteServiceDeal2 = new FavoriteServiceDeal();
        favoriteServiceDeal2.setUser(user);
        favoriteServiceDeal2.setServiceDeal(serviceDeal2);

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);

        when(repository.findFavoriteServiceDealByUser(user))
                .thenReturn(Arrays.asList(favoriteServiceDeal1, favoriteServiceDeal2));

        List<ServiceDeal> favoriteServices = favoriteService.findFavoriteServices();

        assertNotNull(favoriteServices);
        assertEquals(2, favoriteServices.size());
        assertTrue(favoriteServices.contains(serviceDeal1));
        assertTrue(favoriteServices.contains(serviceDeal2));
    }

    @Test
    void findFavoriteServices_NoFavorites() {
        Long userId = 1L;
        String phoneNumber = "+79882505362";

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setId(userId);
        user.setFirstName("Никита");

        when(authentication.getName()).thenReturn(phoneNumber);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userService.findByPhoneNumber(phoneNumber)).thenReturn(user);

        when(repository.findFavoriteServiceDealByUser(user)).thenReturn(Arrays.asList());

        List<ServiceDeal> favoriteServices = favoriteService.findFavoriteServices();

        assertNotNull(favoriteServices);
        assertTrue(favoriteServices.isEmpty());
    }
}