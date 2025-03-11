package org.example.sberproject.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.sberproject.entity.FavoriteServiceDeal;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.User;
import org.example.sberproject.exceptions.FavoriteException;
import org.example.sberproject.repository.FavoriteServiceDealRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceDealService {

    private final FavoriteServiceDealRepository repository;
    private final UserService userService;
    private final ServiceDealService dealService;

    public FavoriteServiceDeal addToFavorite(Long serviceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userService.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceId);
        FavoriteServiceDeal favoriteServiceDeal = new FavoriteServiceDeal();
        favoriteServiceDeal.setUser(user);
        favoriteServiceDeal.setServiceDeal(serviceDeal);
        if (repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal).isPresent()){
            log.error("Услуга с ID: " + serviceId + "уже в избранном");
            throw  new FavoriteException("Услуга с ID: " + serviceId + "уже в избранном");
        }
        log.info("Услуга добавлена в избранное");
        return repository.save(favoriteServiceDeal);
    }

    public void deleteFromFavorites(Long serviceId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userService.findByPhoneNumber(login);
        ServiceDeal serviceDeal = dealService.findById(serviceId);
        Optional<FavoriteServiceDeal> favorite = repository.findFavoriteServiceDealByUserAndServiceDeal(user, serviceDeal);
        if (favorite.isPresent()){
            repository.delete(favorite.get());
        }
        else {
            log.error("Данной услуги нет в избранном");
            throw  new IllegalArgumentException();
        }
    }

    public List<ServiceDeal> findFavoriteServices(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        User user = userService.findByPhoneNumber(login);
        return  repository.findFavoriteServiceDealByUser(user).stream()
                .map(FavoriteServiceDeal::getServiceDeal).toList();
    }

}
