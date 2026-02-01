package io.github.daegwonkim.chronicle.repository.condition;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;

import java.util.List;

public record SearchLogsCondition(
        List<Long> appIds,
        TimeRangeVo timeRange,
        LogLevel logLevel,
        String query,
        int page,
        int size
) {
}
