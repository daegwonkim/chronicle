package io.github.daegwonkim.chronicleapi.dto.logs;

import io.github.daegwonkim.chronicleapi.enumerate.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

public class SaveLogDto {

    @Schema(description = "로그 저장 요청 DTO")
    public record Req(
            Long appId,
            LogLevel logLevel,
            String logMessage,
            Instant loggedAt,
            LogFields logFields
    ) {
        public record LogFields(
                String key,
                Object value
        ) {}
    }
}
