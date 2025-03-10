package org.example.sberproject.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.service.ServiceDealService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceDealController {
    private final ServiceDealService service;

    @GetMapping("/get-all")
    public ResponseEntity<Page<?>> getAllServices(Pageable pageable) {
        Page<?> services = service.getAllServices(pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<?>> getServicesByCategory(
            @PathVariable Category category, Pageable pageable) {
        Page<?> services = service.getServicesByCategory(category, pageable);
        return ResponseEntity.ok(services);
    }

    @GetMapping("my-services")
    public ResponseEntity<Page<AuthServiceDealResponseDto>> getMyServices(Pageable pageable){
        Page<AuthServiceDealResponseDto> myServices = service.getMyServices(pageable);
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

}
