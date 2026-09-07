package com.keepguard.ms_communication.application.dto.common;

public record SearchCriteriaViewDTO(
    int page,
    int size,
    String sortBy,
    String sortDirection
) {
    public static SearchCriteriaViewDTO of(int page, int size) {
        return new SearchCriteriaViewDTO(page, size, null, "ASC");
    }

    public static SearchCriteriaViewDTO of(int page, int size, String sortBy) {
        return new SearchCriteriaViewDTO(page, size, sortBy, "ASC");
    }

    public static SearchCriteriaViewDTO of(int page, int size, String sortBy, String sortDirection) {
        return new SearchCriteriaViewDTO(page, size, sortBy, sortDirection);
    }
}
