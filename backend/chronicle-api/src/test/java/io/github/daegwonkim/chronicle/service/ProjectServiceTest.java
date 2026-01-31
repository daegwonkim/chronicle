package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.projects.CreateProjectDto;
import io.github.daegwonkim.chronicle.dto.projects.GetProjectsDto;
import io.github.daegwonkim.chronicle.dto.projects.ModifyProjectDto;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
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

    @Test
    @DisplayName("관리자 계정과 연결된 프로젝트 목록을 조회한다")
    void getProjects_findProjectsByAdminId() {
        // given
        Long adminId = 1L;
        Project project1 = Project.create(adminId, "Project A", "Description A");
        Project project2 = Project.create(adminId, "Project B", null);
        given(projectRepository.findAllByAdminId(adminId)).willReturn(List.of(project1, project2));

        // when
        GetProjectsDto.Res res = projectService.getProjects(adminId);

        // then
        assertThat(res.projects()).hasSize(2);
        assertThat(res.projects().get(0).name()).isEqualTo("Project A");
        assertThat(res.projects().get(0).description()).isEqualTo("Description A");
        assertThat(res.projects().get(1).name()).isEqualTo("Project B");
        assertThat(res.projects().get(1).description()).isNull();
    }

    @Test
    @DisplayName("프로젝트가 없는 관리자는 빈 목록을 반환한다")
        void getProjects_returnsEmptyListWhenNoProjects() {
            // given
            Long adminId = 999L;
            given(projectRepository.findAllByAdminId(adminId)).willReturn(Collections.emptyList());

            // when
            GetProjectsDto.Res res = projectService.getProjects(adminId);

            // then
            assertThat(res.projects()).isEmpty();
    }

    @Test
    @DisplayName("프로젝트의 이름과 설명을 수정한다")
    void modifyProject_updatesNameAndDescription() {
        // given
        Long projectId = 1L;
        Project project = Project.create(1L, "Old Name", "Old Desc");
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        ModifyProjectDto.Req req = new ModifyProjectDto.Req("New Name", "New Desc");

        // when
        projectService.modifyProject(projectId, req);

        // then
        assertThat(project.getName()).isEqualTo("New Name");
        assertThat(project.getDescription()).isEqualTo("New Desc");
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 수정하면 예외가 발생한다")
    void modifyProject_throwsWhenNotFound() {
        // given
        Long projectId = 999L;
        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        ModifyProjectDto.Req req = new ModifyProjectDto.Req("Name", "Desc");

        // when & then
        assertThatThrownBy(() -> projectService.modifyProject(projectId, req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("프로젝트를 삭제하면 deleted 플래그가 true로 변경된다")
    void deleteProject_setsDeletedTrue() {
        // given
        Long projectId = 1L;
        Project project = Project.create(1L, "Project", "Desc");
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        assertThat(project.getDeleted()).isFalse();

        // when
        projectService.deleteProject(projectId);

        // then
        assertThat(project.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 프로젝트를 삭제하면 예외가 발생한다")
    void deleteProject_throwsWhenNotFound() {
        // given
        Long projectId = 999L;
        given(projectRepository.findById(projectId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> projectService.deleteProject(projectId))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
