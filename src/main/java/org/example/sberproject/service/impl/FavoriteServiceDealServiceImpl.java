package org.example.sberproject.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.entity.FavoriteServiceDeal;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.FavoriteException;
import org.example.sberproject.exceptions.ServiceNotFoundException;
import org.example.sberproject.repository.FavoriteServiceDealRepository;
import org.example.sberproject.service.api.FavoriteServiceDealService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceDealServiceImpl implements FavoriteServiceDealService {

    private final FavoriteServiceDealRepository repository;
    private final UserServiceImpl userServiceImpl;
    private final ServiceDealServiceImpl dealService;

    public FavoriteServiceDeal addToFavorite(Long serviceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userServiceImpl.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceId);
        FavoriteServiceDeal favoriteServiceDeal = new FavoriteServiceDeal();
        favoriteServiceDeal.setUser(user);
        favoriteServiceDeal.setServiceDeal(serviceDeal);
        if (repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal).isPresent()){
            log.error("Услуга с ID: " + serviceId + "уже в избранном");
            throw  new FavoriteException("Услуга с ID: " + serviceId + "уже в избранном");
        }
        if (serviceDeal.getApplicant().getId().equals(user.getId())){
            log.error("Нельзя добавить свою услугу в избранное!");
            throw new FavoriteException("Нельзя добавить свою услугу в избранное!");
        }
        log.info("Услуга добавлена в избранное");
        return repository.save(favoriteServiceDeal);
    }

    public void deleteFromFavorites(Long serviceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userServiceImpl.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceId);
        Optional<FavoriteServiceDeal> favorite = repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal);
        if (favorite.isPresent()){
            repository.delete(favorite.get());
        }
        else {
            log.error("Данной услуги нет в избранном");
            throw new ServiceNotFoundException();
        }
    }

    public List<ServiceDeal> findFavoriteServices(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userServiceImpl.findByPhoneNumber(login);
        return  repository.findFavoriteServiceDealByUser(user).stream()
                .map(FavoriteServiceDeal::getServiceDeal).toList();
    }

}
