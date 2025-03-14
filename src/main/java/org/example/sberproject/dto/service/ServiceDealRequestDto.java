package org.example.sberproject.dto.service;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.sberproject.entity.Category;

import java.util.List;

@Data
public class ServiceDealRequestDto {
    @NotNull(message = "Категория услуги не может быть пустой")
    private Category categoryService;

    @NotBlank(message = "Описание услуги не может быть пустым")
    private String descriptionService;

    @NotNull(message = "Ожидаемые категории не могут быть пустыми")
    private List<Category> awaitingCategoryService;


    private String awaitDescriptionService;

}
