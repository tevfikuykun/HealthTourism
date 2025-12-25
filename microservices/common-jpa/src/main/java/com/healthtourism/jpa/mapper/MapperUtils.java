package com.healthtourism.jpa.mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper Utility Class
 * 
 * Provides utility methods for common mapping operations.
 */
public class MapperUtils {
    
    /**
     * Map collection using provided mapper function
     * 
     * @param source Source collection
     * @param mapper Mapper function
     * @param <S> Source type
     * @param <T> Target type
     * @return Mapped list
     */
    public static <S, T> List<T> mapList(Collection<S> source, java.util.function.Function<S, T> mapper) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream()
            .map(mapper)
            .collect(Collectors.toList());
    }
    
    /**
     * Map collection, filtering null values
     * 
     * @param source Source collection
     * @param mapper Mapper function
     * @param <S> Source type
     * @param <T> Target type
     * @return Mapped list (null values filtered out)
     */
    public static <S, T> List<T> mapListFilterNull(Collection<S> source, java.util.function.Function<S, T> mapper) {
        if (source == null || source.isEmpty()) {
            return Collections.emptyList();
        }
        return source.stream()
            .map(mapper)
            .filter(java.util.Objects::nonNull)
            .collect(Collectors.toList());
    }
    
    /**
     * Safely map object, returning null if source is null
     * 
     * @param source Source object
     * @param mapper Mapper function
     * @param <S> Source type
     * @param <T> Target type
     * @return Mapped object or null
     */
    public static <S, T> T mapOrNull(S source, java.util.function.Function<S, T> mapper) {
        if (source == null) {
            return null;
        }
        return mapper.apply(source);
    }
    
    /**
     * Copy non-null fields from source to target
     * 
     * @param source Source object
     * @param target Target object
     * @param copier Copier function
     * @param <S> Source type
     * @param <T> Target type
     */
    public static <S, T> void copyNotNull(S source, T target, java.util.function.BiConsumer<S, T> copier) {
        if (source != null && target != null) {
            copier.accept(source, target);
        }
    }
}

