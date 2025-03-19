package org.example.sberproject.service.api;

import org.example.sberproject.entity.ServiceDeal;
import org.example.sberproject.entity.FavoriteServiceDeal;

import java.util.List;

/**
 * Сервис для управления избранными сделками пользователей.
 */
public interface FavoriteServiceDealService {

    /**
     * Добавляет сделку в список избранных.
     *
     * @param serviceId ID сделки, которую необходимо добавить в избранное.
     * @return Объект {@link FavoriteServiceDeal}, представляющий избранную сделку.
     * @throws org.example.sberproject.exceptions.FavoriteException Если сделка уже в избранном или является собственной.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделка с данным ID не найдена.
     */
    FavoriteServiceDeal addToFavorite(Long serviceId) throws org.example.sberproject.exceptions.FavoriteException, org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Удаляет сделку из списка избранных.
     *
     * @param serviceId ID сделки, которую необходимо удалить из избранного.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделка с данным ID не найдена в списке избранных.
     */
    void deleteFromFavorites(Long serviceId) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Получает список всех избранных сделок пользователя.
     *
     * @return Список объектов {@link ServiceDeal}, добавленных в избранное.
     */
    List<ServiceDeal> findFavoriteServices();
}
