package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.CreateProjectDto;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public CreateProjectDto.Res createProject(CreateProjectDto.Req req) {
        Project project = Project.create(req.name(), req.description());
        projectRepository.save(project);

        return new CreateProjectDto.Res(project.getApiKey());
    }
}
