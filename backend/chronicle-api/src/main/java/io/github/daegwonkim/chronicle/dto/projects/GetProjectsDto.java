package io.github.daegwonkim.chronicle.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetProjectsDto {

    @Schema(description = "프로젝트 목록 조회 응답 DTO")
    public record Res(
            Long id,
            String name,
            String description
    ) {}
}
