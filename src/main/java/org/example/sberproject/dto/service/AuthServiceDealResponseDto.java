package org.example.sberproject.dto.service;

import lombok.Data;
import org.example.sberproject.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthServiceDealResponseDto {
    private Long id;
    private Category categoryService;
    private String descriptionService;

    private List<Category> awaitingCategoryService;
    private String awaitDescriptionService;
    private Long userId;
    private String phoneNumber;
    private String email;
    private LocalDateTime dateOfPublication;
}