package org.example.sberproject.controller;

import lombok.RequiredArgsConstructor;
import org.example.sberproject.dto.service.ServiceResponseDto;
import org.example.sberproject.entity.ResponseStatus;
import org.example.sberproject.service.impl.ServiceResponseServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class ServiceResponseController {
    private final ServiceResponseServiceImpl service;

    //Просмотр всех кто откликнулся на нашу услугу по id
    @GetMapping("/responses/{serviceDealId}")
    public ResponseEntity<List<ServiceResponseDto>> getAllResponsesForService(@PathVariable Long serviceDealId) {
        List<ServiceResponseDto> responses = service.getResponsesForMyService(serviceDealId);
        return ResponseEntity.ok(responses);
    }

    //Возможность ответить на отклик принять/удалить
    @PutMapping("/{serviceResponseId}/status")
    public ResponseEntity<String> updateStatus(@PathVariable Long serviceResponseId,
                                               @RequestParam ResponseStatus newStatus) {
        service.updateResponseStatus(serviceResponseId, newStatus);
        return ResponseEntity.ok("Статус отклика успешно обновлен");
    }

    //Отклик на услугу
    @PostMapping("/{serviceId}/respond")
    public ResponseEntity<String> respondToService(@PathVariable Long serviceId,
                                                   @RequestParam Long offeredServiceId) {
        service.responseToService(serviceId, offeredServiceId);
        return new ResponseEntity<>("Вы успешно откликнулись на услугу!", HttpStatus.OK);
    }

    // Мои отклики на заявки
    @GetMapping("/responses")
    public ResponseEntity<List<ServiceResponseDto>> getAllMyResponses(){
        List<ServiceResponseDto> allMyResponse = service.getAllMyResponse();
        return  new ResponseEntity<>(allMyResponse, HttpStatus.OK);
    }

}
