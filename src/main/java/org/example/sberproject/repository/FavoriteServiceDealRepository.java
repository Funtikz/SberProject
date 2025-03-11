package org.example.sberproject.repository;

import org.example.sberproject.entity.FavoriteServiceDeal;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteServiceDealRepository extends JpaRepository<FavoriteServiceDeal, Long> {
    Optional<FavoriteServiceDeal> findFavoriteServiceDealByUserAndServiceDeal(User user, ServiceDeal serviceDeal);
    @EntityGraph(attributePaths = {"serviceDeal"})
    List<FavoriteServiceDeal> findFavoriteServiceDealByUser(User user);
}
