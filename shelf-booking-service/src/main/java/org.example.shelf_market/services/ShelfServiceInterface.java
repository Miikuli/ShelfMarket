package org.example.shelf_market.services;

import org.example.shelf_market.dto.ShelfDTO;
import java.util.List;

public interface ShelfServiceInterface {
    List<ShelfDTO> getAllShelves();
    ShelfDTO getShelfById(Integer id);
    List<ShelfDTO> getShelvesByGroup(Integer shelfGroupNumber);
    ShelfDTO createShelf(ShelfDTO shelfDTO);
    ShelfDTO updateShelf(Integer id, ShelfDTO shelfDTO);
    void deleteShelf(Integer id);
}