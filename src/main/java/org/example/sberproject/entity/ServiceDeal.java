package org.example.sberproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_deal")
public class ServiceDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category categoryService;

    private String descriptionService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    User applicant;

    @ElementCollection(targetClass = Category.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "service_deal_categories",
            joinColumns = @JoinColumn(name = "service_deal_id"))
    private List<Category> awaitingCategoryService;

    // TODO Добавить поле которое будет обозначать находиться во всех запросах или нет

    private String awaitDescriptionService;

    private LocalDateTime dateOfPublication;

}
