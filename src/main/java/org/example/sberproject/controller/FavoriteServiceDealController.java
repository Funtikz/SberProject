package org.example.sberproject.controller;


import lombok.RequiredArgsConstructor;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.service.FavoriteServiceDealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteServiceDealController {

    private final FavoriteServiceDealService favoriteServiceDealService;

    @PostMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addToFavorite(@PathVariable Long serviceId) {
        favoriteServiceDealService.addToFavorite(serviceId);
        return new ResponseEntity<>("Услуга добавлена в избранное", HttpStatus.CREATED);
    }

    @DeleteMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String>deleteFromFavorites(@PathVariable Long serviceId) {
        favoriteServiceDealService.deleteFromFavorites(serviceId);
        return new ResponseEntity<>("Услуга удалена из избранного", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ServiceDeal>> findFavoriteServices() {
        List<ServiceDeal> favoriteServices = favoriteServiceDealService.findFavoriteServices();
        return new ResponseEntity<>(favoriteServices, HttpStatus.OK);
    }
}