package io.github.daegwonkim.chronicle.dto.logs;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.vo.LogVo;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class SearchLogsDto {
    @Schema(description = "로그 검색 요청 DTO")
    public record Req(
            @Schema(description = "앱 ID 목록")
            List<Long> appIds,

            @Schema(description = "시간 범위")
            TimeRangeVo timeRange,

            @Schema(description = "로그 레벨 목록")
            List<LogLevel> logLevels,

            @Schema(description = "검색어", example = "timed out...")
            String query,

            @Schema(description = "마지막으로 받은 로그 ID (첫 요청 시 null)")
            Long cursorId,

            @Schema(description = "페이지 사이즈", example = "20")
            int size
    ) {}

    @Schema(description = "로그 검색 응답 DTO")
    public record Res(
            @Schema(description = "검색된 로그 목록")
            List<LogVo> logs,

            @Schema(description = "다음 페이지 존재 여부")
            boolean hasNext,

            @Schema(description = "예상 총 개수 (최대 10,001, 첫 요청에서만 반환)")
            Long estimatedCount
    ) {}
}
