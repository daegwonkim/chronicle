package io.github.daegwonkim.chronicle.dto.logs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GetProjectsDto {
    @Schema(description = "프로젝트 목록 조회 요청 DTO")
    public record Res(
            @Schema(description = "프로젝트 목록")
            List<Project> projects
    ) {
        @Schema(description = "프로젝트 요소")
        public record Project(
                @Schema(description = "프로젝트 PK", example = "0")
                Long id,

                @Schema(description = "프로젝트명", example = "My Project")
                String name,

                @Schema(description = "프로젝트 설명", example = "My first project")
                String description
        ) {}
    }
}
