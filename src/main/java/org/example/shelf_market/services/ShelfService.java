package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.entities.User;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.ShelfGroupRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.example.shelf_market.command.*;
import org.example.shelf_market.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("shelfService")
public class ShelfService implements ShelfServiceInterface {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ShelfGroupRepository shelfGroupRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @Autowired
    private UserRepository userRepository;

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

    public ShelfDTO createShelf(ShelfDTO shelfDTO) {
        Shelf shelf = convertToEntity(shelfDTO);
        Shelf saved = shelfRepository.save(shelf);
        return dtoFactory.createShelfDTO(saved);
    }

    public ShelfDTO updateShelf(Integer id, ShelfDTO shelfDTO) {
        Shelf existing = shelfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelf not found"));

        existing.setBooked(shelfDTO.getBooked());

        if (!existing.getShelfGroup().getNumber().equals(shelfDTO.getShelfGroupNumber())) {
            ShelfGroup newGroup = shelfGroupRepository.findById(shelfDTO.getShelfGroupNumber())
                    .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));
            existing.setShelfGroup(newGroup);
        }

        Shelf updated = shelfRepository.save(existing);
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

        // ИСПРАВЛЕНИЕ: устанавливаем пользователя только если он указан и полка забронирована
        if (shelfDTO.getUserId() != null && Boolean.TRUE.equals(shelfDTO.getBooked())) {
            User user = userRepository.findById(shelfDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            shelf.setUser(user);
        } else {
            shelf.setUser(null);  // Явно устанавливаем null для свободной полки
        }

        return shelf;
    }
}