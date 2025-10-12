package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class LoggingShelfServiceDecorator implements ShelfServiceInterface {

    private final ShelfServiceInterface targetService;

    @Autowired
    public LoggingShelfServiceDecorator(@Qualifier("shelfService") ShelfServiceInterface targetService) {
        this.targetService = targetService;
    }

    @Override
    public List<ShelfDTO> getAllShelves() {
        System.out.println("🟡 [SHELF SERVICE] Getting all shelves");
        long startTime = System.currentTimeMillis();

        List<ShelfDTO> result = targetService.getAllShelves();

        long endTime = System.currentTimeMillis();
        System.out.println("🟢 [SHELF SERVICE] Retrieved " + result.size() + " shelves in " + (endTime - startTime) + "ms");
        return result;
    }

    @Override
    public ShelfDTO getShelfById(Integer id) {
        System.out.println("🟡 [SHELF SERVICE] Getting shelf by ID: " + id);

        ShelfDTO result = targetService.getShelfById(id);

        System.out.println("🟢 [SHELF SERVICE] Retrieved shelf: " + (result != null ? "found" : "not found"));
        return result;
    }

    @Override
    public ShelfDTO createShelf(ShelfDTO shelfDTO) {
        System.out.println("🟡 [SHELF SERVICE] Creating new shelf in group: " + shelfDTO.getShelfGroupNumber());

        ShelfDTO result = targetService.createShelf(shelfDTO);

        System.out.println("🟢 [SHELF SERVICE] Created shelf with ID: " + result.getId());
        return result;
    }

    @Override
    public ShelfDTO updateShelf(Integer id, ShelfDTO shelfDTO) {
        System.out.println("🟡 [SHELF SERVICE] Updating shelf ID: " + id);

        ShelfDTO result = targetService.updateShelf(id, shelfDTO);

        System.out.println("🟢 [SHELF SERVICE] Updated shelf ID: " + id);
        return result;
    }

    @Override
    public void deleteShelf(Integer id) {
        System.out.println("🔴 [SHELF SERVICE] Deleting shelf ID: " + id);

        targetService.deleteShelf(id);

        System.out.println("🟢 [SHELF SERVICE] Deleted shelf ID: " + id);
    }

    @Override
    public List<ShelfDTO> getShelvesByGroup(Integer shelfGroupNumber) {
        System.out.println("🟡 [SHELF SERVICE] Getting shelves by group: " + shelfGroupNumber);

        List<ShelfDTO> result = targetService.getShelvesByGroup(shelfGroupNumber);

        System.out.println("🟢 [SHELF SERVICE] Retrieved " + result.size() + " shelves from group " + shelfGroupNumber);
        return result;
    }
}