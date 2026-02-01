package io.github.daegwonkim.chronicle.dto.projects;

import io.github.daegwonkim.chronicle.vo.ProjectVo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class SearchProjectsDto {
    @Schema(description = "프로젝트 목록 조회 요청 DTO")
    public record Req(
            @Schema(description = "검색어", example = "my project")
            String query,

            @Schema(description = "페이지", example = "0")
            int page,

            @Schema(description = "페이지 크기", example = "20")
            int size
    ) {}

    @Schema(description = "프로젝트 목록 조회 응답 DTO")
    public record Res(
            @Schema(description = "프로젝트 목록")
            List<ProjectVo> projects,

            @Schema(description = "검색된 프로젝트 총 개수", example = "10")
            Long totalCount
    ) {}
}
