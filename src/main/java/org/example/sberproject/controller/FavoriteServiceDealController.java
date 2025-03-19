package org.example.sberproject.controller;


import lombok.RequiredArgsConstructor;
import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.service.impl.FavoriteServiceDealServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteServiceDealController {

    private final FavoriteServiceDealServiceImpl favoriteServiceDealServiceImpl;

    @PostMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addToFavorite(@PathVariable Long serviceId) {
        favoriteServiceDealServiceImpl.addToFavorite(serviceId);
        return new ResponseEntity<>("Услуга добавлена в избранное", HttpStatus.CREATED);
    }

    @DeleteMapping("/{serviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String>deleteFromFavorites(@PathVariable Long serviceId) {
        favoriteServiceDealServiceImpl.deleteFromFavorites(serviceId);
        return new ResponseEntity<>("Услуга удалена из избранного", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ServiceDeal>> findFavoriteServices() {
        List<ServiceDeal> favoriteServices = favoriteServiceDealServiceImpl.findFavoriteServices();
        return new ResponseEntity<>(favoriteServices, HttpStatus.OK);
    }
}