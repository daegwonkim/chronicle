package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.projects.CreateProjectDto;
import io.github.daegwonkim.chronicle.dto.projects.SearchProjectsDto;
import io.github.daegwonkim.chronicle.dto.projects.ModifyProjectDto;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import io.github.daegwonkim.chronicle.repository.condition.SearchProjectsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchProjectsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public CreateProjectDto.Res createProject(CreateProjectDto.Req req) {
        Project project = Project.create(0L, req.name(), req.description());
        projectRepository.save(project);

        return new CreateProjectDto.Res(project.getApiKey());
    }

    @Transactional(readOnly = true)
    public SearchProjectsDto.Res searchProjects(Long adminId, SearchProjectsDto.Req req) {
        SearchProjectsCondition condition = new SearchProjectsCondition(adminId, req.query(), req.page(), req.size());
        SearchProjectsResult result = projectRepository.search(condition);

        return new SearchProjectsDto.Res(result.projects(), result.totalCount());
    }

    @Transactional
    public void modifyProject(Long id, ModifyProjectDto.Req req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        project.modify(req.name(), req.description());
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));

        project.delete();
    }
}
