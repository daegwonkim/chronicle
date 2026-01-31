package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.CreateProjectDto;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("새로운 프로젝트를 생성하고 저장한 뒤 API 키를 발급한다")
    void createProject_savesProjectAndReturnsApiKey() {
        // given
        CreateProjectDto.Req req = new CreateProjectDto.Req("My Project", "Project description");

        // when
        CreateProjectDto.Res res = projectService.createProject(req);

        // then
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());

        Project saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("My Project");
        assertThat(saved.getDescription()).isEqualTo("Project description");
        assertThat(saved.getApiKey()).isNotNull();
        assertThat(res.apiKey()).isEqualTo(saved.getApiKey());
    }
}
