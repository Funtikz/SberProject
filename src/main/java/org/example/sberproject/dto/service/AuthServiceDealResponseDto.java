package org.example.sberproject.dto.service;

import lombok.Data;
import org.example.sberproject.entity.Category;

import java.time.LocalDateTime;

@Data
public class AuthServiceDealResponseDto {
    private Long id;
    private Category categoryService;
    private String descriptionService;
    private Category awaitingCategoryService;
    private String awaitCategoryDescription;
    private Long userId;
    private String phoneNumber;
    private String email;
    private LocalDateTime dateOfPublication;
}