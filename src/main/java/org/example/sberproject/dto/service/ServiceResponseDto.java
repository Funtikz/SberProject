package org.example.sberproject.dto.service;


import lombok.Data;
import org.example.sberproject.dto.user.UserRatingDto;
import org.example.sberproject.entity.ResponseStatus;

import java.time.LocalDateTime;

@Data
public class ServiceResponseDto {
    private Long id;
    private ResponseStatus responseStatus;
    private UserRatingDto rating;
    private NotAuthServiceDealResponseDto serviceDeal;
    private LocalDateTime dateOfResponse;
    private NotAuthServiceDealResponseDto offeredServiceDeal;
}
