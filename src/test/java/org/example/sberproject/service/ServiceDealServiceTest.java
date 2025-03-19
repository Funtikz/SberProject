package org.example.sberproject.service;

import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.dto.service.NotAuthServiceDealResponseDto;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.ServiceNotFoundException;
import org.example.sberproject.repository.ServiceDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ServiceDealServiceTest {

    @Autowired
    private ServiceDealService service;

    @MockitoBean
    private ServiceDealRepository repository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityContext securityContext;

    @MockitoBean
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("123@gmail.com");
        testUser.setPhoneNumber("+79882505362");
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(testUser.getPhoneNumber());
        when(userService.findByPhoneNumber(testUser.getPhoneNumber())).thenReturn(testUser);
    }

    @Test
    void createService_Successful() {
        ServiceDealRequestDto requestDto = new ServiceDealRequestDto();
        requestDto.setCategoryService(Category.IT);
        requestDto.setDescriptionService("IT услуга");

        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(1L);
        serviceDeal.setCategoryService(Category.IT);
        serviceDeal.setDescriptionService("IT услуга");
        serviceDeal.setApplicant(testUser);
        serviceDeal.setDateOfPublication(LocalDateTime.now());

        when(repository.save(any(ServiceDeal.class))).thenReturn(serviceDeal);

        AuthServiceDealResponseDto responseDto = service.createService(requestDto);

        assertNotNull(responseDto);
        assertEquals(serviceDeal.getId(), responseDto.getId());
        assertEquals(serviceDeal.getCategoryService(), responseDto.getCategoryService());
    }

    @Test
    void updateService_Successful() {
        Long serviceId = 1L;
        ServiceDealRequestDto requestDto = new ServiceDealRequestDto();
        requestDto.setCategoryService(Category.DESIGN);
        requestDto.setDescriptionService("Updated description");

        ServiceDeal existingService = new ServiceDeal();
        existingService.setId(serviceId);
        existingService.setApplicant(testUser);

        when(repository.findById(serviceId)).thenReturn(Optional.of(existingService));
        when(repository.save(any(ServiceDeal.class))).thenReturn(existingService);

        AuthServiceDealResponseDto responseDto = service.updateService(serviceId, requestDto);

        assertNotNull(responseDto);
        assertEquals(Category.DESIGN, responseDto.getCategoryService());
        assertEquals("Updated description", responseDto.getDescriptionService());
    }

    @Test
    void deleteService_Successful() {
        Long serviceId = 1L;
        ServiceDeal serviceDeal = new ServiceDeal();
        serviceDeal.setId(serviceId);
        serviceDeal.setApplicant(testUser);

        when(repository.findById(serviceId)).thenReturn(Optional.of(serviceDeal));
        doNothing().when(repository).delete(serviceDeal);

        assertDoesNotThrow(() -> service.deleteService(serviceId));
        verify(repository, times(1)).delete(serviceDeal);
    }

    @Test
    void deleteService_NotFound() {
        Long serviceId = 1L;
        when(repository.findById(serviceId)).thenReturn(Optional.empty());
        assertThrows(ServiceNotFoundException.class, () -> service.deleteService(serviceId));
    }

    @Test
    void getMyServices_Successful() {
        Pageable pageable = Pageable.ofSize(10);
        ServiceDeal service1 = new ServiceDeal();
        service1.setId(1L);
        service1.setApplicant(testUser);
        service1.setCategoryService(Category.IT);
        service1.setDescriptionService("My IT service");

        List<ServiceDeal> serviceList = List.of(service1);
        Page<ServiceDeal> page = new PageImpl<>(serviceList, pageable, serviceList.size());
        when(repository.findByApplicant(testUser, pageable)).thenReturn(page);

        Page<NotAuthServiceDealResponseDto> result = service.getMyServices(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchServicesByKeyword_Successful() {
        Pageable pageable = Pageable.ofSize(10);
        String keyword = "example";
        ServiceDeal service1 = new ServiceDeal();
        service1.setId(1L);
        service1.setDescriptionService("An example service");

        List<ServiceDeal> services = List.of(service1);
        Page<ServiceDeal> page = new PageImpl<>(services, pageable, services.size());
        when(repository.findByDescriptionServiceContainingIgnoreCase(keyword, pageable)).thenReturn(page);

        Page<NotAuthServiceDealResponseDto> result = service.searchServicesByKeyword(pageable, keyword);
        assertEquals(1, result.getTotalElements());
    }
}
