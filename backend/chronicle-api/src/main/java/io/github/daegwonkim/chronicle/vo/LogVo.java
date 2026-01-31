package io.github.daegwonkim.chronicle.vo;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;

import java.time.Instant;

public record LogVo(
        Long id,
        String appName,
        LogLevel level,
        String message,
        String logger,
        Instant loggedAt
) {
}
