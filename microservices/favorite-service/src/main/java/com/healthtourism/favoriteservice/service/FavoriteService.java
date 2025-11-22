package com.healthtourism.favoriteservice.service;
import com.healthtourism.favoriteservice.dto.FavoriteDTO;
import com.healthtourism.favoriteservice.entity.Favorite;
import com.healthtourism.favoriteservice.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    public List<FavoriteDTO> getFavoritesByUser(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    public List<FavoriteDTO> getFavoritesByUserAndType(Long userId, String itemType) {
        return favoriteRepository.findByUserIdAndItemType(userId, itemType)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public FavoriteDTO addFavorite(Long userId, String itemType, Long itemId) {
        favoriteRepository.findByUserIdAndItemTypeAndItemId(userId, itemType, itemId)
                .ifPresent(f -> { throw new RuntimeException("Zaten favorilerde"); });
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setItemType(itemType);
        favorite.setItemId(itemId);
        favorite.setCreatedAt(LocalDateTime.now());
        return convertToDTO(favoriteRepository.save(favorite));
    }
    
    @Transactional
    public void removeFavorite(Long userId, String itemType, Long itemId) {
        Favorite favorite = favoriteRepository.findByUserIdAndItemTypeAndItemId(userId, itemType, itemId)
                .orElseThrow(() -> new RuntimeException("Favori bulunamadı"));
        favoriteRepository.delete(favorite);
    }
    
    private FavoriteDTO convertToDTO(Favorite favorite) {
        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setUserId(favorite.getUserId());
        dto.setItemType(favorite.getItemType());
        dto.setItemId(favorite.getItemId());
        dto.setCreatedAt(favorite.getCreatedAt());
        return dto;
    }
}

