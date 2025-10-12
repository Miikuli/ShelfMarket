package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfDTO;
import org.example.shelf_market.entities.Shelf;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.repositories.ShelfRepository;
import org.example.shelf_market.repositories.ShelfGroupRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("shelfService")
public class ShelfService implements ShelfServiceInterface {

    @Autowired
    private ShelfRepository shelfRepository;

    @Autowired
    private ShelfGroupRepository shelfGroupRepository;

    @Autowired
    private DtoFactory dtoFactory;
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

    private Shelf convertToEntity(ShelfDTO shelfDTO) {
        Shelf shelf = new Shelf();
        shelf.setId(shelfDTO.getId());
        shelf.setBooked(shelfDTO.getBooked());

        ShelfGroup shelfGroup = shelfGroupRepository.findById(shelfDTO.getShelfGroupNumber())
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));
        shelf.setShelfGroup(shelfGroup);

        return shelf;
    }
}