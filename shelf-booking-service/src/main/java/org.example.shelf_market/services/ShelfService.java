package org.example.shelf_market.services;

import org.example.shelf_market.client.UserServiceClient;
import org.example.shelf_market.command.BookShelfCommand;
import org.example.shelf_market.command.CancelBookingCommand;
import org.example.shelf_market.command.Command;
import org.example.shelf_market.command.CommandInvoker;
import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.ShelfGroupRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("shelfService")
public class ShelfService implements ShelfServiceInterface {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ShelfGroupRepository shelfGroupRepository;

    @Autowired
    private ShelfGroupService shelfGroupService;

    @Autowired
    private DtoFactory dtoFactory;

    @Autowired
    private UserServiceClient userServiceClient;  // ← Feign Client вместо UserRepository

    @PersistenceContext
    private EntityManager em;

    private final CommandInvoker commandInvoker = new CommandInvoker();

    public List<ShelfDTO> getAllShelves() {
        return shelfRepository.findAll().stream()
                .map(dtoFactory::createShelfDTO)
                .collect(Collectors.toList());
    }

    public ShelfDTO getShelfById(Integer id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));
        return dtoFactory.createShelfDTO(shelf);
    }

    public List<ShelfDTO> getShelvesByGroup(Integer shelfGroupNumber) {
        return shelfRepository.findByShelfGroup_Number(shelfGroupNumber).stream()
                .map(dtoFactory::createShelfDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ShelfDTO createShelf(ShelfDTO shelfDTO) {
        System.out.println("Creating shelf with id: " + shelfDTO.getId() + ", group: " + shelfDTO.getShelfGroupNumber());
        if (shelfDTO.getId() != null && shelfRepository.existsById(shelfDTO.getId())) {
            throw new RuntimeException("Shelf with id " + shelfDTO.getId() + " already exists");
        }
        Shelf shelf = convertToEntity(shelfDTO);
        shelf = shelfRepository.save(shelf);
        return dtoFactory.createShelfDTO(shelf);
    }

    public ShelfDTO updateShelf(Integer id, ShelfDTO shelfDTO) {
        Shelf existing = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        // Сохраняем старое состояние для проверки изменения статуса
        Boolean oldBookedStatus = existing.getBooked();
        Integer groupNumber = existing.getShelfGroup().getNumber();

        // Обновляем только те поля, которые пришли в запросе (не null)
        if (shelfDTO.getBooked() != null) {
            // Логика изменения статуса бронирования
            if (Boolean.TRUE.equals(shelfDTO.getBooked()) && Boolean.FALSE.equals(existing.getBooked())) {
                // Бронирование полки
                if (shelfDTO.getUserId() != null) {
                    // Проверяем существование пользователя через Feign Client
                    validateUserExists(shelfDTO.getUserId());
                    existing.setUserId(shelfDTO.getUserId());  // ← Сохраняем только ID
                } else {
                    throw new RuntimeException("User ID is required for booking");
                }
            } else if (Boolean.FALSE.equals(shelfDTO.getBooked()) && Boolean.TRUE.equals(existing.getBooked())) {
                // Освобождение полки
                existing.setUserId(null);
            }
            existing.setBooked(shelfDTO.getBooked());
        }

        // Обновляем пользователя если указан
        if (shelfDTO.getUserId() != null) {
            // Проверяем существование пользователя
            validateUserExists(shelfDTO.getUserId());
            existing.setUserId(shelfDTO.getUserId());
            // Если указан пользователь, полка считается забронированной
            if (shelfDTO.getBooked() == null) {
                existing.setBooked(true);
            }
        }

        // Обновляем группу если указана
        if (shelfDTO.getShelfGroupNumber() != null &&
                !existing.getShelfGroup().getNumber().equals(shelfDTO.getShelfGroupNumber())) {
            ShelfGroup newGroup = shelfGroupRepository.findById(shelfDTO.getShelfGroupNumber())
                    .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));
            existing.setShelfGroup(newGroup);
        }

        // Если полка освобождается (booked = false), убираем пользователя
        if (Boolean.FALSE.equals(shelfDTO.getBooked())) {
            existing.setUserId(null);
        }

        Shelf updated = shelfRepository.save(existing);

        // Если изменился статус бронирования, обновляем группу
        if (!Objects.equals(oldBookedStatus, updated.getBooked())) {
            shelfGroupService.updateGroupBookingStatus(groupNumber);
        }

        return dtoFactory.createShelfDTO(updated);
    }

    public void deleteShelf(Integer id) {
        shelfRepository.deleteById(id);
    }

    public void bookShelf(Integer shelfId, UUID userId) {
        Command command = new BookShelfCommand(this, shelfId, userId);
        commandInvoker.executeCommand(command);
    }

    // Отменить бронирование через паттерн Command
    public void cancelBooking(Integer id) {
        Command command = new CancelBookingCommand(this, id);
        commandInvoker.executeCommand(command);
    }

    // Отменить последнюю операцию (undo)
    public void undoLastAction() {
        commandInvoker.undoLast();
    }

    public void bookShelfInternal(Integer id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));
        if (Boolean.TRUE.equals(shelf.getBooked())) {
            System.out.println("Полка уже забронирована.");
            return;
        }
        shelf.setBooked(true);
        shelfRepository.save(shelf);
        System.out.println("Полка " + id + " успешно забронирована.");
    }

    public void cancelBookingInternal(Integer id) {
        Shelf shelf = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));
        if (Boolean.FALSE.equals(shelf.getBooked())) {
            System.out.println("Полка уже свободна.");
            return;
        }
        shelf.setBooked(false);
        shelf.setUserId(null);  // ← Очищаем userId
        shelfRepository.save(shelf);
        System.out.println("Бронирование полки " + id + " отменено.");
    }

    private Shelf convertToEntity(ShelfDTO shelfDTO) {
        Shelf shelf = new Shelf();
        shelf.setId(shelfDTO.getId());
        shelf.setBooked(shelfDTO.getBooked());

        // Находим группу полок
        ShelfGroup shelfGroup = shelfGroupRepository.findById(shelfDTO.getShelfGroupNumber())
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));
        shelf.setShelfGroup(shelfGroup);

        // Устанавливаем userId только если он указан и полка забронирована
        if (shelfDTO.getUserId() != null && Boolean.TRUE.equals(shelfDTO.getBooked())) {
            // Проверяем существование пользователя
            validateUserExists(shelfDTO.getUserId());
            shelf.setUserId(shelfDTO.getUserId());
        } else {
            shelf.setUserId(null);  // Явно устанавливаем null для свободной полки
        }

        return shelf;
    }

    // Метод для проверки существования пользователя через Feign Client
    private void validateUserExists(UUID userId) {
        try {
            Boolean exists = userServiceClient.userExists(userId);
            if (exists == null || !exists) {
                throw new RuntimeException("User not found: " + userId);
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Cannot connect to User Service: " + e.getMessage());
        }
    }
}