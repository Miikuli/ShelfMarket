package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfGroupDTO;
import org.example.shelf_market.entities.ShelfGroup;
import org.example.shelf_market.repositories.ShelfGroupRepository;
import org.example.shelf_market.dto.DtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShelfGroupService {

    @Autowired
    private ShelfGroupRepository shelfGroupRepository;

    @Autowired
    private DtoFactory dtoFactory;

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

    public ShelfGroupDTO createShelfGroup(ShelfGroupDTO shelfGroupDTO) {
        ShelfGroup shelfGroup = convertToEntity(shelfGroupDTO);
        ShelfGroup saved = shelfGroupRepository.save(shelfGroup);
        return dtoFactory.createShelfGroupDTO(saved);
    }

    public ShelfGroupDTO updateShelfGroup(Integer number, ShelfGroupDTO shelfGroupDTO) {
        ShelfGroup existing = shelfGroupRepository.findById(number)
                .orElseThrow(() -> new RuntimeException("ShelfGroup not found"));

        existing.setBooked(shelfGroupDTO.getBooked());
        ShelfGroup updated = shelfGroupRepository.save(existing);
        return dtoFactory.createShelfGroupDTO(updated);
    }

    public void deleteShelfGroup(Integer number) {
        shelfGroupRepository.deleteById(number);
    }

    private ShelfGroup convertToEntity(ShelfGroupDTO shelfGroupDTO) {
        ShelfGroup shelfGroup = new ShelfGroup();
        shelfGroup.setNumber(shelfGroupDTO.getNumber());
        shelfGroup.setBooked(shelfGroupDTO.getBooked());
        return shelfGroup;
    }
}