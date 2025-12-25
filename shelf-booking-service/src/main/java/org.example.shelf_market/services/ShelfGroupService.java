package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfGroupDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.repositories.ShelfGroupRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.example.shelf_market.repositories.ShelfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShelfGroupService {

    private static final Logger logger = LoggerFactory.getLogger(ShelfGroupService.class);

    @Autowired
    private ShelfGroupRepository shelfGroupRepository;

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private DtoFactory dtoFactory;

    @PersistenceContext
    private EntityManager em;

    public List<ShelfGroupDTO> getAllShelfGroups() {
        return shelfGroupRepository.findAll().stream()
                .map(dtoFactory::createShelfGroupDTO)
                .collect(Collectors.toList());
    }

    public ShelfGroupDTO getShelfGroupById(Integer number) {
        ShelfGroup shelfGroup = shelfGroupRepository.findById(number)
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));
        return dtoFactory.createShelfGroupDTO(shelfGroup);
    }

    @Transactional
    public ShelfGroupDTO createShelfGroup(ShelfGroupDTO shelfGroupDTO) {
        logger.info("Creating shelf group with DTO number: {}, booked: {}", shelfGroupDTO.getNumber(), shelfGroupDTO.getBooked());
        ShelfGroup shelfGroup = convertToEntity(shelfGroupDTO);
        logger.info("Converted entity number: {}, booked: {}", shelfGroup.getNumber(), shelfGroup.getBooked());
        em.merge(shelfGroup);
        logger.info("Persisted entity number: {}", shelfGroup.getNumber());
        return dtoFactory.createShelfGroupDTO(shelfGroup);
    }

    /**
     * Безопасное обновление группы с проверкой бизнес-правил
     */
    @Transactional
    public ShelfGroupDTO updateShelfGroup(Integer number, ShelfGroupDTO shelfGroupDTO) {
        ShelfGroup existing = shelfGroupRepository.findById(number)
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));

        // Проверяем бизнес-правило: нельзя освободить группу если есть забронированные полки
        if (Boolean.TRUE.equals(existing.getBooked()) &&
                Boolean.FALSE.equals(shelfGroupDTO.getBooked())) {

            if (!canSetGroupAvailable(number)) {
                throw new RuntimeException(
                        "Cannot set group to available because it has booked shelves. " +
                                "First free all shelves in the group."
                );
            }
        }
        existing.setBooked(shelfGroupDTO.getBooked());
        ShelfGroup updated = shelfGroupRepository.save(existing);
        return dtoFactory.createShelfGroupDTO(updated);
    }

    @Transactional
    public void updateGroupBookingStatus(Integer groupNumber) {
        ShelfGroup shelfGroup = shelfGroupRepository.findById(groupNumber)
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));

        // Находим все полки в этой группе
        List<Shelf> shelvesInGroup = shelfRepository.findByShelfGroup_Number(groupNumber);

        if (shelvesInGroup.isEmpty()) {
            // Если в группе нет полок, статус false
            shelfGroup.setBooked(false);
        } else {
            // Проверяем забронированы ли ВСЕ полки в группе
            boolean allShelvesBooked = shelvesInGroup.stream()
                    .allMatch(shelf -> Boolean.TRUE.equals(shelf.getBooked()));

            shelfGroup.setBooked(allShelvesBooked);
        }

        shelfGroupRepository.save(shelfGroup);
    }


    public void deleteShelfGroup(Integer number) {
        shelfGroupRepository.deleteById(number);
    }

    private ShelfGroup convertToEntity(ShelfGroupDTO shelfGroupDTO) {
        logger.info("Converting DTO number: {}, booked: {}", shelfGroupDTO.getNumber(), shelfGroupDTO.getBooked());
        ShelfGroup shelfGroup = new ShelfGroup();
        shelfGroup.setNumber(shelfGroupDTO.getNumber());
        shelfGroup.setBooked(shelfGroupDTO.getBooked() != null ? shelfGroupDTO.getBooked() : false);
        logger.info("Created entity number: {}, booked: {}", shelfGroup.getNumber(), shelfGroup.getBooked());
        return shelfGroup;
    }

    public boolean canSetGroupAvailable(Integer groupNumber) {
        // Находим все забронированные полки в этой группе
        List<Shelf> bookedShelves = shelfRepository.findByShelfGroup_NumberAndBooked(groupNumber, true);

        // Если есть хотя бы одна забронированная полка - нельзя освобождать группу
        return bookedShelves.isEmpty();
    }


}