package com.smarttask.backend.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

// Format de pagination UNIQUE pour toute l'API -- le frontend n'a besoin de
// connaitre qu'une seule forme de reponse paginee, quel que soit l'endpoint.
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
