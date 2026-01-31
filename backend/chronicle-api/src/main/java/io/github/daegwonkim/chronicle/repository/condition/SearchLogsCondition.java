package io.github.daegwonkim.chronicle.repository.condition;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;

public record SearchLogsCondition(
        Long appId,
        TimeRangeVo timeRange,
        LogLevel logLevel,
        String query,
        int page,
        int size
) {
}
