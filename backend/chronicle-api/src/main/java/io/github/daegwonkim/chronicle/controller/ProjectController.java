package io.github.daegwonkim.chronicle.controller;

import io.github.daegwonkim.chronicle.dto.logs.CreateProjectDto;
import io.github.daegwonkim.chronicle.dto.logs.GetProjectsDto;
import io.github.daegwonkim.chronicle.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "새로운 프로젝트 생성", description = "새로운 프로젝트를 생성하고, API 키를 발급합니다.")
    @PostMapping
    public CreateProjectDto.Res createProject(CreateProjectDto.Req req) {
        return projectService.createProject(req);
    }

    @Operation(summary = "프로젝트 목록 조회", description = "관리자 계정에 연결된 프로젝트 목록을 조회합니다.")
    public GetProjectsDto.Res getProjects() {
        return projectService.getProjects(0L);
    }
}
