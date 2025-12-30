package org.example.shelf_market.facade;  // Измените пакет

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.example.shelf_market.observer.ShelfSubject;
import org.example.shelf_market.exceptions.*;
import org.example.shelf_market.services.ShelfGroupService;
import org.example.shelf_market.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class BookingFacade {

    private static final Logger logger = LoggerFactory.getLogger(BookingFacade.class);
    private static final int MAX_BOOKINGS_PER_USER = 5;

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ShelfGroupService shelfGroupService;

    @Autowired
    private DtoFactory dtoFactory;

    @Autowired
    private ShelfSubject shelfSubject;

    @Autowired
    private UserServiceClient userServiceClient;  // ← Feign Client вместо UserRepository

    /**
     * Бронирование полки для пользователя
     */
    @Transactional
    public ShelfDTO bookShelf(Integer shelfId, UUID userId) {
        logger.info("Attempting to book shelf {} for user {}", shelfId, userId);

        try {
            // 1. Находим полку
            Shelf shelf = shelfRepository.findById(shelfId)
                    .orElseThrow(() -> new ShelfNotFoundException(shelfId));

            // 2. Проверяем существование пользователя через Feign Client
            validateUserExists(userId);

            // 3. Проверяем доступность полки
            if (Boolean.TRUE.equals(shelf.getBooked())) {
                throw new ShelfAlreadyBookedException(shelfId);
            }

            // 4. Проверяем лимит бронирований пользователя
            validateUserBookingLimit(userId);

            // 5. Бронируем полку
            shelf.setBooked(true);
            shelf.setUserId(userId);  // ← Сохраняем только ID пользователя
            Shelf updatedShelf = shelfRepository.save(shelf);

            // 6. Обновляем статус группы
            shelfGroupService.updateGroupBookingStatus(shelf.getShelfGroup().getNumber());

            // 7. Уведомляем наблюдателей
            String message = String.format("Полка забронирована пользователем %s",
                    userId);
            shelfSubject.notifyObservers(shelfId, message);

            logger.info("Successfully booked shelf {} for user {}", shelfId, userId);

            // 8. Возвращаем DTO
            return dtoFactory.createShelfDTO(updatedShelf);

        } catch (RuntimeException e) {
            logger.error("Failed to book shelf {} for user {}: {}", shelfId, userId, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public ShelfDTO cancelBooking(Integer shelfId, UUID currentUserId, boolean isAdmin) {
        logger.info("Attempting to cancel booking for shelf {} by user {}", shelfId, currentUserId);

        try {
            // 1. Находим полку
            Shelf shelf = shelfRepository.findById(shelfId)
                    .orElseThrow(() -> new ShelfNotFoundException(shelfId));

            // 2. Проверяем, что полка действительно забронирована
            if (Boolean.FALSE.equals(shelf.getBooked())) {
                throw new ShelfNotBookedException(shelfId);
            }

            // 3. Проверяем авторизацию: пользователь должен быть владельцем брони или админом
            if (!shelf.getUserId().equals(currentUserId) && !isAdmin) {
                throw new UnauthorizedCancellationException("Unauthorized to cancel this booking");
            }

            // 3. Сохраняем номер группы для обновления статуса
            Integer groupNumber = shelf.getShelfGroup().getNumber();
            UUID userId = shelf.getUserId();  // Сохраняем userId для сообщения

            // 4. Освобождаем полку
            shelf.setBooked(false);
            shelf.setUserId(null);  // ← Очищаем userId
            Shelf updatedShelf = shelfRepository.save(shelf);

            // 5. Обновляем статус группы
            shelfGroupService.updateGroupBookingStatus(groupNumber);

            // 6. Уведомляем наблюдателей
            String message = String.format("Бронирование отменено (было забронировано пользователем %s)",
                    userId != null ? userId.toString() : "unknown");
            shelfSubject.notifyObservers(shelfId, message);

            logger.info("Successfully canceled booking for shelf {}", shelfId);

            // 7. Возвращаем DTO
            return dtoFactory.createShelfDTO(updatedShelf);

        } catch (RuntimeException e) {
            logger.error("Failed to cancel booking for shelf {}: {}", shelfId, e.getMessage());
            throw e;
        }
    }

    /**
     * Проверка доступности полки
     */
    @Transactional(readOnly = true)
    public boolean isShelfAvailable(Integer shelfId) {
        try {
            return shelfRepository.findById(shelfId)
                    .map(shelf -> !shelf.getBooked())
                    .orElseThrow(() -> new ShelfNotFoundException(shelfId));
        } catch (ShelfNotFoundException e) {
            logger.warn("Shelf not found while checking availability: {}", shelfId);
            return false;
        }
    }

    /**
     * Получение информации о полке
     */
    @Transactional(readOnly = true)
    public ShelfDTO getShelfInfo(Integer shelfId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new ShelfNotFoundException(shelfId));
        return dtoFactory.createShelfDTO(shelf);
    }

    /**
     * Проверка лимита бронирований пользователя
     */
    private void validateUserBookingLimit(UUID userId) {
        // Теперь ищем по полю userId (UUID), а не по связи с User
        long userActiveBookings = shelfRepository.countByUserIdAndBookedTrue(userId);

        if (userActiveBookings >= MAX_BOOKINGS_PER_USER) {
            throw new MaxBookingsExceededException(userId, MAX_BOOKINGS_PER_USER);
        }
    }

    /**
     * Получение количества активных бронирований пользователя
     */
    @Transactional(readOnly = true)
    public long getUserActiveBookingsCount(UUID userId) {
        // Проверяем существование пользователя через Feign Client
        validateUserExists(userId);

        // Считаем бронирования по userId
        return shelfRepository.countByUserIdAndBookedTrue(userId);
    }

    /**
     * Проверка существования пользователя через Feign Client
     */
    private void validateUserExists(UUID userId) {
        try {
            Boolean exists = userServiceClient.userExists(userId);
            if (exists == null || !exists) {
                throw new UserNotFoundException(userId);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Cannot connect to User Service: " + e.getMessage());
        }
    }
}