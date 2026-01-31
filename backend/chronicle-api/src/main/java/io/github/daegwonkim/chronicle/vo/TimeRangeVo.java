package io.github.daegwonkim.chronicle.vo;

import java.time.Instant;

public record TimeRangeVo(
        Instant from,
        Instant to
) {
}
