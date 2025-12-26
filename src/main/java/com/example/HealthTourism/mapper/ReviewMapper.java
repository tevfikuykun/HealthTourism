package com.example.HealthTourism.mapper;

import com.example.HealthTourism.dto.ReviewDTO;
import com.example.HealthTourism.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for converting between Review entity and ReviewDTO
 * Automatically generates implementation at compile time
 */
@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);
    
    /**
     * Converts Review entity to ReviewDTO
     * Custom mappings for nested entity fields
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(review.getUser().getFirstName() + \" \" + review.getUser().getLastName())")
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "doctorName", expression = "java(review.getDoctor() != null ? review.getDoctor().getTitle() + \" \" + review.getDoctor().getFirstName() + \" \" + review.getDoctor().getLastName() : null)")
    @Mapping(target = "hospitalId", source = "hospital.id")
    @Mapping(target = "hospitalName", source = "hospital.name")
    ReviewDTO toDto(Review review);
    
    /**
     * Converts ReviewDTO to Review entity
     * Note: Relationships should be set manually in service layer
     */
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "hospital", ignore = true)
    Review toEntity(ReviewDTO dto);
    
    /**
     * Converts list of Review entities to list of ReviewDTOs
     */
    List<ReviewDTO> toDtoList(List<Review> reviews);
}

