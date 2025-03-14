package org.example.sberproject.service;

import org.example.sberproject.dto.service.NotAuthServiceDealResponseDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.repository.ServiceDealRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class ServiceServiceTest {

    @Autowired
    ServiceDealService service;

    @MockitoBean
    ServiceDealRepository repository;

    @MockitoBean
    UserService userService;

    @MockitoBean
    SecurityContext securityContext;

    @MockitoBean
    private Authentication authentication;

    private User testUser;

    @BeforeEach
    public void setUp(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("123@gmail.com");
        testUser.setPassword("1234567NN");
        testUser.setFirstName("Vasia");
        testUser.setLastName("Shervakov");
        testUser.setPhoneNumber("+79882505362");
        SecurityContextHolder.setContext(securityContext);
    }


    @AfterEach
    public void afterEach(){
        Mockito.reset(repository);
        Mockito.reset(userService);
        Mockito.reset(securityContext);
        Mockito.reset(authentication);
    }

    @Test
    void getAllServices_Successful() {
        Pageable pageable = Pageable.ofSize(10);
        Long serviceId = 1L;
        ServiceDeal service1 = new ServiceDeal();
        service1.setId(serviceId);
        service1.setApplicant(testUser);
        service1.setCategoryService(Category.IT);
        service1.setDescriptionService("Просто пример");

        List<ServiceDeal> services = List.of(service1);

        Page<ServiceDeal> servicePage = new PageImpl<>(services, pageable, services.size());
        when(repository.findAll(pageable)).thenReturn(servicePage);

        Page<NotAuthServiceDealResponseDto> result = service.getAllServices(pageable);

        assertEquals(1, result.getContent().size());

    }

    @Test
    void getServicesByCategory_Successful() {
        Pageable pageable = Pageable.ofSize(10);
        Long serviceId1 = 1L;
        Long serviceId2 = 2L;

        ServiceDeal service1 = new ServiceDeal();
        service1.setId(serviceId1);
        service1.setApplicant(testUser);
        service1.setCategoryService(Category.IT);
        service1.setDescriptionService("IT услуга");

        ServiceDeal service2 = new ServiceDeal();
        service2.setId(serviceId2);
        service2.setApplicant(testUser);
        service2.setCategoryService(Category.DESIGN);
        service2.setDescriptionService("Дизайн услуга");

        List<Category> categoryList = List.of(Category.IT, Category.DESIGN);
        List<ServiceDeal> serviceDeals = List.of(service1, service2);

        Page<ServiceDeal> page = new PageImpl<>(serviceDeals, pageable, serviceDeals.size());

        when(repository.findByCategoryServiceIn(categoryList, pageable)).thenReturn(page);

        Page<NotAuthServiceDealResponseDto> result = service.getServicesByCategory(categoryList, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(Category.IT, result.getContent().get(0).getCategoryService());
        assertEquals(Category.DESIGN, result.getContent().get(1).getCategoryService());
    }

    // TODO Переделать тест исходя из SpringContextHolder
//    @Test
//    void createService() {
//        Long serviceId = 1L;
//        Long userId = testUser.getId();
//
//        ServiceDealRequestDto serviceDealRequestDto = new ServiceDealRequestDto();
//        serviceDealRequestDto.setCategoryService(Category.IT);
//
//        ServiceDeal serviceDeal = new ServiceDeal();
//        serviceDeal.setId(serviceId);
//        serviceDeal.setCategoryService(Category.IT);
//        serviceDeal.setApplicant(testUser);
//
//        when(userService.findById(userId)).thenReturn(testUser);
//        when(repository.save(any(ServiceDeal.class))).thenReturn(serviceDeal);
//
//        AuthServiceDealResponseDto responseDto = service.createService(serviceDealRequestDto);
//
//        assertNotNull(responseDto);
//        assertEquals(serviceId, responseDto.getId());
//        assertEquals(Category.IT, responseDto.getCategoryService());
//        assertEquals(userId, responseDto.getUserId());
//
//    }

}