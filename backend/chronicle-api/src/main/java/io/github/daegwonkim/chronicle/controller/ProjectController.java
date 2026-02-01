package io.github.daegwonkim.chronicle.controller;

import io.github.daegwonkim.chronicle.dto.projects.CreateProjectDto;
import io.github.daegwonkim.chronicle.dto.projects.GetProjectDto;
import io.github.daegwonkim.chronicle.dto.projects.SearchProjectsDto;
import io.github.daegwonkim.chronicle.dto.projects.ModifyProjectDto;
import io.github.daegwonkim.chronicle.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "새로운 프로젝트 생성", description = "새로운 프로젝트를 생성하고, API 키를 발급합니다.")
    @PostMapping
    public CreateProjectDto.Res createProject(@RequestBody CreateProjectDto.Req req) {
        return projectService.createProject(req);
    }

    @Operation(summary = "프로젝트 목록 조회", description = "관리자 계정에 연결된 프로젝트 목록을 조회합니다.")
    @GetMapping
    public SearchProjectsDto.Res searchProjects(@ModelAttribute SearchProjectsDto.Req req) {
        return projectService.searchProjects(0L, req);
    }

    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public GetProjectDto.Res getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @Operation(summary = "프로젝트 수정", description = "프로젝트명, 설명 등을 수정합니다.")
    @PutMapping("/{id}")
    public void modifyProject(@PathVariable Long id, @RequestBody ModifyProjectDto.Req req) {
        projectService.modifyProject(id, req);
    }

    @Operation(summary = "프로젝트 삭제", description = "프로젝트를 삭제합니다.")
    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
