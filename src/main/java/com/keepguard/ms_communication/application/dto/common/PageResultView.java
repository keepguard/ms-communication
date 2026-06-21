package com.keepguard.ms_communication.application.dto.common;

import java.util.List;

public record PageResultView<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious
) {
    public static <T> PageResultView<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResultView<>(
            content,
            page,
            size,
            totalElements,
            totalPages,
            page < totalPages - 1,
            page > 0
        );
    }
}
