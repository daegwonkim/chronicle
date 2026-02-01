package io.github.daegwonkim.chronicle.repository.condition;

public record SearchProjectsCondition(
        Long adminId,
        String query,
        int page,
        int size
) {
}
