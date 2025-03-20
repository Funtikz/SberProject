package org.example.sberproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "service_response")
public class ServiceResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_deal_id", nullable = false)
    private ServiceDeal serviceDeal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offered_service_deal_id")
    private ServiceDeal offeredServiceDeal;

    @Enumerated
    private ResponseStatus responseStatus = ResponseStatus.PENDING;

    private LocalDateTime dateOfResponse;
}
