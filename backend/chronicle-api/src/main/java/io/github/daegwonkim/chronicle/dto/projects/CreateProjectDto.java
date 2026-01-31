package io.github.daegwonkim.chronicle.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public class CreateProjectDto {

    @Schema(description = "프로젝트 생성 요청 DTO")
    public record Req(
            @Schema(description = "프로젝트명", example = "My Project")
            String name,

            @Schema(description = "프로젝트 설명", example = "This is my first project")
            String description
    ) {}

    @Schema(description = "프로젝트 생성 응답 DTO")
    public record Res(
            @Schema(description = "로그 저장 및 조회 요청에서 사용할 API 키", example = "0c3ff5ef-be52-4fb5-8494-43c94e004e5f")
            UUID apiKey
    ) {}
}
