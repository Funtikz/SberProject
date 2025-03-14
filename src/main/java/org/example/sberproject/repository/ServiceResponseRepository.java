package org.example.sberproject.repository;

import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.ServiceResponse;
import org.example.sberproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceResponseRepository extends JpaRepository<ServiceResponse, Long> {
    Optional<ServiceResponse> findByServiceDealAndUser(ServiceDeal serviceDeal, User user);

    List<ServiceResponse> findAllByUser (User user);
    @Query("SELECT sd FROM ServiceResponse sr JOIN sr.serviceDeal sd WHERE sr.user.id =:userId")
    List<ServiceDeal> findAllServiceDealByUser(Long userId);
    List<ServiceResponse> findAllByServiceDeal(ServiceDeal serviceDeal);

    List<ServiceResponse> findAlLByUser(User user);
}
