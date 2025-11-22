package com.healthtourism.favoriteservice.controller;
import com.healthtourism.favoriteservice.dto.FavoriteDTO;
import com.healthtourism.favoriteservice.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUser(userId));
    }
    
    @GetMapping("/user/{userId}/type/{itemType}")
    public ResponseEntity<List<FavoriteDTO>> getFavoritesByUserAndType(@PathVariable Long userId, @PathVariable String itemType) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserAndType(userId, itemType));
    }
    
    @PostMapping
    public ResponseEntity<?> addFavorite(@RequestParam Long userId, @RequestParam String itemType, @RequestParam Long itemId) {
        try {
            return ResponseEntity.ok(favoriteService.addFavorite(userId, itemType, itemId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping
    public ResponseEntity<?> removeFavorite(@RequestParam Long userId, @RequestParam String itemType, @RequestParam Long itemId) {
        try {
            favoriteService.removeFavorite(userId, itemType, itemId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

