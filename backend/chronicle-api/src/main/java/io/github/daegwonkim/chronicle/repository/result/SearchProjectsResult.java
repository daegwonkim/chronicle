package io.github.daegwonkim.chronicle.repository.result;

import io.github.daegwonkim.chronicle.vo.ProjectVo;

import java.util.List;

public record SearchProjectsResult(
        List<ProjectVo> projects,
        Long totalCount
) {
}
