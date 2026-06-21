package com.keepguard.ms_communication.application.dto.common;

public record SearchCriteriaView(
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    public static SearchCriteriaView of(int page, int size) {
        return new SearchCriteriaView(page, size, null, "ASC");
    }

    public static SearchCriteriaView of(int page, int size, String sortBy) {
        return new SearchCriteriaView(page, size, sortBy, "ASC");
    }

    public static SearchCriteriaView of(int page, int size, String sortBy, String sortDirection) {
        return new SearchCriteriaView(page, size, sortBy, sortDirection);
    }
}
