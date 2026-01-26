package io.github.daegwonkim.chronicle.dto.projects;

import io.swagger.v3.oas.annotations.media.Schema;

public class CreateProjectDto {

    @Schema(description = "프로젝트 생성 요청 DTO")
    public record Req(

            @Schema(description = "프로젝트명", example = "Chronicle")
            String name,

            @Schema(description = "프로젝트 설명", example = "대용량 분산 로그 저장 시스템")
            String description
    ) {}

    @Schema(description = "프로젝트 생성 응답 DTO")
    public record Res(

            @Schema(description = "생성된 프로젝트 ID", example = "0")
            Long id
    ) {}
}
