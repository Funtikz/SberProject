package org.example.sberproject.service.api;

import org.example.sberproject.dto.service.ServiceResponseDto;
import org.example.sberproject.entity.ResponseStatus;

import java.util.List;

/**
 * Сервис для управления откликами на сделки (услуги) пользователей.
 */
public interface ServiceResponseService {

    /**
     * Обновляет статус отклика на сделку.
     *
     * @param serviceResponseId ID отклика, который нужно обновить.
     * @param newStatus        Новый статус отклика {@link ResponseStatus}.
     * @throws org.example.sberproject.exceptions.ServiceNotFoundException Если отклик с данным ID не найден.
     * @throws org.example.sberproject.exceptions.ResponseException Если отклик уже был обработан.
     */
    void updateResponseStatus(Long serviceResponseId, ResponseStatus newStatus) throws org.example.sberproject.exceptions.ServiceNotFoundException, org.example.sberproject.exceptions.ResponseException;

    /**
     * Получает все отклики, сделанные текущим пользователем на различные сделки.
     *
     * @return Список объектов {@link ServiceResponseDto}, представляющих отклики пользователя.
     */
    List<ServiceResponseDto> getAllMyResponse();

    /**
     * Получает отклики, сделанные на услугу, предложенную текущим пользователем.
     *
     * @param serviceDealId ID сделки, для которой получаем отклики.
     * @return Список объектов {@link ServiceResponseDto}, представляющих отклики на указанную услугу.
     * @throws org.example.sberproject.exceptions.ResponseException Если текущий пользователь не является владельцем услуги.
     */
    List<ServiceResponseDto> getResponsesForMyService(Long serviceDealId) throws org.example.sberproject.exceptions.ResponseException;

    /**
     * Откликается на услугу, предлагая свою услугу для обмена.
     *
     * @param serviceId        ID услуги, на которую пользователь откликается.
     * @param offeredServiceId ID услуги, которую предлагает пользователь в ответ.
     * @throws org.example.sberproject.exceptions.ResponseException Если пользователь уже откликался на эту услугу или откликается на свою собственную услугу.
     */
    void responseToService(Long serviceId, Long offeredServiceId) throws org.example.sberproject.exceptions.ResponseException;
}
