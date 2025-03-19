package org.example.sberproject.service.api;

import org.example.sberproject.dto.service.AuthServiceDealResponseDto;
import org.example.sberproject.dto.service.NotAuthServiceDealResponseDto;
import org.example.sberproject.dto.service.ServiceDealRequestDto;
import org.example.sberproject.entity.Category;
import org.example.sberproject.entity.ServiceDeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Сервис для управления сделками (услугами) пользователей.
 */
public interface ServiceDealService {

    /**
     * Получает список всех доступных сделок.
     *
     * @param pageable Объект {@link Pageable} для пагинации результатов.
     * @return Страница объектов {@link NotAuthServiceDealResponseDto}, содержащая список сделок.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделки не найдены.
     */
    Page<NotAuthServiceDealResponseDto> getAllServices(Pageable pageable) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Выполняет поиск сделок по указанным категориям.
     *
     * @param category Список категорий для фильтрации сделок.
     * @param pageable Объект {@link Pageable} для пагинации результатов.
     * @return Страница объектов {@link NotAuthServiceDealResponseDto}, содержащая найденные сделки.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделки не найдены.
     */
    Page<NotAuthServiceDealResponseDto> searchServicesByCategory(List<Category> category, Pageable pageable) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Получает список сделок, созданных текущим авторизованным пользователем.
     *
     * @param pageable Объект {@link Pageable} для пагинации результатов.
     * @return Страница объектов {@link NotAuthServiceDealResponseDto}, содержащая сделки пользователя.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделки не найдены для пользователя.
     */
    Page<NotAuthServiceDealResponseDto> getMyServices(Pageable pageable) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Выполняет поиск сделок по ключевому слову.
     *
     * @param pageable Объект {@link Pageable} для пагинации результатов.
     * @param message  Ключевое слово для поиска.
     * @return Страница объектов {@link NotAuthServiceDealResponseDto}, содержащая найденные сделки.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделки не найдены по ключевому слову.
     */
    Page<NotAuthServiceDealResponseDto> searchServicesByKeyword(Pageable pageable, String message) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Создает новую сделку на основе переданных данных.
     *
     * @param requestDto DTO с информацией о создаваемой сделке.
     * @return Объект {@link AuthServiceDealResponseDto}, представляющий созданную сделку.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если произошла ошибка при создании сделки.
     */
    AuthServiceDealResponseDto createService(ServiceDealRequestDto requestDto) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Обновляет существующую сделку.
     *
     * @param serviceId  ID сделки, которую необходимо обновить.
     * @param requestDto DTO с новой информацией о сделке.
     * @return Объект {@link AuthServiceDealResponseDto}, содержащий обновленные данные сделки.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделка с таким ID не найдена.
     */
    AuthServiceDealResponseDto updateService(Long serviceId, ServiceDealRequestDto requestDto) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Удаляет сделку по ее ID.
     *
     * @param serviceId ID сделки, которую необходимо удалить.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделка с таким ID не найдена.
     */
    void deleteService(Long serviceId) throws org.example.sberproject.exceptions.ServiceNotFoundException;

    /**
     * Находит сделку по ее ID.
     *
     * @param serviceId ID сделки.
     * @return Объект {@link ServiceDeal}, представляющий найденную сделку.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если сделка с таким ID не найдена.
     */
    ServiceDeal findById(Long serviceId) throws org.example.sberproject.exceptions.ServiceNotFoundException;
}
