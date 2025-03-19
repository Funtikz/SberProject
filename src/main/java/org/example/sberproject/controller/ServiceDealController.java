package org.example.sberproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.NotAuthServiceDealResponseDto;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.service.impl.ServiceDealServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceDealController {
    private final ServiceDealServiceImpl service;

    @GetMapping("/get-all")
    public ResponseEntity<Page<NotAuthServiceDealResponseDto>> getAllServices(Pageable pageable) {
        Page<NotAuthServiceDealResponseDto> services = service.getAllServices(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/category")
    public ResponseEntity<Page<NotAuthServiceDealResponseDto>> getServicesByCategories(
            @RequestParam List<Category> categories, Pageable pageable) {
        Page<NotAuthServiceDealResponseDto> services = service.searchServicesByCategory(categories, pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/services/search")
    public ResponseEntity<Page<NotAuthServiceDealResponseDto>> searchServices(@RequestParam String keyword,
                                                                              Pageable pageable){
        Page<NotAuthServiceDealResponseDto> servicesBySearch = service.searchServicesByKeyword(pageable, keyword);
        return ResponseEntity.ok(servicesBySearch);
    }

    @GetMapping("my-services")
    public ResponseEntity<Page<NotAuthServiceDealResponseDto>> getMyServices(Pageable pageable){
        Page<NotAuthServiceDealResponseDto> myServices = service.getMyServices(pageable);
        return  ResponseEntity.ok(myServices);
    }

    @PostMapping
    public ResponseEntity<AuthServiceDealResponseDto> createService(
            @Valid @RequestBody ServiceDealRequestDto requestDto) {
        AuthServiceDealResponseDto createdService = service.createService(requestDto);
        return ResponseEntity.ok(createdService);
    }

    @PutMapping("/update/{serviceId}")
    public ResponseEntity<AuthServiceDealResponseDto> update(
            @PathVariable("serviceId") Long serviceId, @RequestBody ServiceDealRequestDto dto){
        AuthServiceDealResponseDto result = service.updateService(serviceId, dto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{serviceId}")
    public ResponseEntity<String> deleteService(@PathVariable("serviceId") Long serviceId){
        service.deleteService(serviceId);
        return  new ResponseEntity<>("Услуга успешно удалена!", HttpStatus.OK);
    }
}
