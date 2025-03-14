package org.example.sberproject.dto.user;

import lombok.Data;

@Data
public class UserRatingDto {
    private Long id;
    private Integer successfulExchanges;
    private Double rating;
}
