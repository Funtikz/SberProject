package org.example.sberproject.repository;

import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDealRepository extends JpaRepository<ServiceDeal, Long> {
    Page<ServiceDeal> findByCategoryService(Category category, Pageable pageable);
    Page<ServiceDeal> findByApplicant(User applicant, Pageable pageable);
    Page<ServiceDeal> findByCategoryServiceIn(List<Category> categories, Pageable pageable);
}
