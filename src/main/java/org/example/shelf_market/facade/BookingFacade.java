package org.example.shelf_market.facade;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.example.shelf_market.observer.ShelfSubject;
import org.example.shelf_market.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class BookingFacade {

    private static final Logger logger = LoggerFactory.getLogger(BookingFacade.class);
    private static final int MAX_BOOKINGS_PER_USER = 5; // Максимум броней на пользователя

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @Autowired
    private ShelfSubject shelfSubject; // Теперь управляется Spring

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

            // 2. Находим пользователя
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            // 3. Проверяем доступность полки
            if (Boolean.TRUE.equals(shelf.getBooked())) {
                throw new ShelfAlreadyBookedException(shelfId);
            }

            // 4. Проверяем лимит бронирований пользователя
            validateUserBookingLimit(userId);

            // 5. Бронируем полку
            shelf.setBooked(true);
            shelf.setUser(user);
            Shelf updatedShelf = shelfRepository.save(shelf);

            // 6. Уведомляем наблюдателей
            String message = String.format("Полка забронирована пользователем %s (%s)",
                    user.getUsername(), userId);
            shelfSubject.notifyObservers(shelfId, message);

            logger.info("Successfully booked shelf {} for user {}", shelfId, user.getUsername());

            // 7. Возвращаем DTO
            return dtoFactory.createShelfDTO(updatedShelf);

        } catch (RuntimeException e) {
            logger.error("Failed to book shelf {} for user {}: {}", shelfId, userId, e.getMessage());
            throw e; // Пробрасываем исключение дальше для обработки в контроллере
        }
    }

    /**
     * Отмена бронирования полки
     */
    @Transactional
    public ShelfDTO cancelBooking(Integer shelfId) {
        logger.info("Attempting to cancel booking for shelf {}", shelfId);

        try {
            // 1. Находим полку
            Shelf shelf = shelfRepository.findById(shelfId)
                    .orElseThrow(() -> new ShelfNotFoundException(shelfId));

            // 2. Проверяем, что полка действительно забронирована
            if (Boolean.FALSE.equals(shelf.getBooked())) {
                throw new ShelfNotBookedException(shelfId);
            }

            // 3. Сохраняем информацию о пользователе для уведомления
            String previousUser = shelf.getUser() != null ?
                    shelf.getUser().getUsername() : "unknown";

            // 4. Освобождаем полку
            shelf.setBooked(false);
            shelf.setUser(null);
            Shelf updatedShelf = shelfRepository.save(shelf);

            // 5. Уведомляем наблюдателей
            String message = String.format("Бронирование отменено (было забронировано пользователем %s)",
                    previousUser);
            shelfSubject.notifyObservers(shelfId, message);

            logger.info("Successfully canceled booking for shelf {}", shelfId);

            // 6. Возвращаем DTO
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
        // Этот метод нужно реализовать в ShelfRepository
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
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return shelfRepository.countByUserIdAndBookedTrue(userId);
    }
}