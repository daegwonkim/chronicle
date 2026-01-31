package io.github.daegwonkim.chronicle.dto.logs;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.vo.LogVo;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;

import java.util.List;

public class SearchLogsDto {
    public record Req(
            Long appId,
            TimeRangeVo timeRange,
            LogLevel logLevel,
            String query,
            int page,
            int size
    ) {}

    public record Res(
            List<LogVo> logs,
            long totalCount
    ) {}
}
