package io.github.daegwonkim.chronicle.dto.logs;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

public class SaveLogsDto {

    @Schema(description = "로그 저장 요청 DTO")
    public record Req(
            @Schema(description = "저장할 로그 목록")
            List<LogEntry> logs
    ) {
        @Schema(description = "로그 객체")
        public record LogEntry(
                @Schema(description = "로그 레벨", example = "DEBUG")
                LogLevel level,

                @Schema(description = "로그 메시지", example = "로그 메시지입니다")
                String message,

                @Schema(description = "로거 이름", example = "TestLogger")
                String logger,

                @Schema(description = "로깅 시간", example = "2026-01-28T08:58:57.910+09:00")
                Instant loggedAt
        ) {}
    }
}
