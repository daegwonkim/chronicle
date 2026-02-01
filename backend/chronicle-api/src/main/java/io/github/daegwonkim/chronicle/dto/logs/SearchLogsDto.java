package io.github.daegwonkim.chronicle.dto.logs;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.vo.LogVo;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class SearchLogsDto {
    @Schema(description = "로그 검색 요청 DTO")
    public record Req(
            @Schema(description = "앱 ID 목록", example = "0")
            List<Long> appIds,

            @Schema(description = "시간 범위")
            TimeRangeVo timeRange,

            @Schema(description = "로그 레벨", example = "DEBUG")
            LogLevel logLevel,

            @Schema(description = "검색어", example = "timed out...")
            String query,

            @Schema(description = "조회할 페이지", example = "0")
            int page,

            @Schema(description = "페이지 사이즈", example = "20")
            int size
    ) {}

    @Schema(description = "로그 검색 응답 DTO")
    public record Res(
            @Schema(description = "검색된 로그 목록")
            List<LogVo> logs,

            @Schema(description = "검색된 로그 총 개수", example = "100")
            long totalCount
    ) {}
}
