package org.example.shelf_market.facade;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.UserRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.example.shelf_market.observer.*;

import java.util.UUID;

@Component
public class BookingFacade {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DtoFactory dtoFactory; // Используем вашу фабрику
    private final ShelfSubject subject = new ShelfSubject();

    @Transactional
    public ShelfDTO bookShelf(Integer shelfId, UUID userId) {
        // 1. Находим полку
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        // 2. Находим пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3. Проверяем доступность
        if (shelf.getBooked()) {
            throw new RuntimeException("Shelf is already booked");
        }

        // 4. Бронируем
        shelf.setBooked(true);
        shelf.setUser(user);
        Shelf updatedShelf = shelfRepository.save(shelf);
        subject.notifyObservers(shelfId, "Полка забронирована пользователем " + userId);
        System.out.println("✅ Shelf " + shelfId + " booked by user " + user.getUsername());

        // 5. Возвращаем DTO через фабрику
        return dtoFactory.createShelfDTO(updatedShelf);
    }

    @Transactional
    public ShelfDTO cancelBooking(Integer shelfId) {
        Shelf shelf = shelfRepository.findById(shelfId)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        shelf.setBooked(false);
        shelf.setUser(null);
        Shelf updatedShelf = shelfRepository.save(shelf);
        subject.notifyObservers(shelfId, "Бронирование отменено");
        System.out.println("✅ Booking canceled for shelf " + shelfId);

        return dtoFactory.createShelfDTO(updatedShelf);
    }

    // Дополнительный полезный метод
    @Transactional(readOnly = true)
    public boolean isShelfAvailable(Integer shelfId) {
        return shelfRepository.findById(shelfId)
                .map(shelf -> !shelf.getBooked())
                .orElse(false);
    }
}