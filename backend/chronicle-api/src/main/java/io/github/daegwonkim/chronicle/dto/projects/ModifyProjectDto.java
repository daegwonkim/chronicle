package io.github.daegwonkim.chronicle.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;

public class ModifyProjectDto {
    @Schema(description = "프로젝트 수정 요청 DTO")
    public record Req(
            @Schema(description = "프로젝트명", example = "New project name")
            String name,

            @Schema(description = "프로젝트 설명", example = "new project description")
            String description
    ) {}
}
