package org.example.sberproject.dto.service;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.sberproject.entity.Category;

@Data
public class ServiceDealRequestDto {
    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    @NotNull(message = "Категория услуги не может быть пустой")
    private Category categoryService;

    @NotBlank(message = "Описание услуги не может быть пустым")
    private String descriptionService;

    @NotNull(message = "Ожидаемая категория услуги не может быть пустой")
    private Category awaitingCategoryService;

    private String awaitCategoryDescription;

}
