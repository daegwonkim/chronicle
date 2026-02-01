package io.github.daegwonkim.chronicle.dto.projects;

import io.github.daegwonkim.chronicle.vo.ApplicationVo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GetProjectDto {
    @Schema(description = "프로젝트 상세 조회 응답 DTO")
    public record Res(
            @Schema(description = "프로젝트 ID", example = "0")
            Long id,

            @Schema(description = "프로젝트명", example = "My Project")
            String name,

            @Schema(description = "프로젝트 설명", example = "My project description")
            String description,

            @Schema(description = "포함된 애플리케이션 목록")
            List<ApplicationVo> applications
    ) {}
}
