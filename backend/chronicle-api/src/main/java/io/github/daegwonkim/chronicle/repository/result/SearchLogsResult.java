package io.github.daegwonkim.chronicle.repository.result;

import io.github.daegwonkim.chronicle.vo.LogVo;

import java.util.List;

public record SearchLogsResult(
        List<LogVo> logs,
        Long totalCount
) {
}
