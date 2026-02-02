package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.dto.logs.SearchLogsDto;
import io.github.daegwonkim.chronicle.entity.Application;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.repository.ApplicationRepository;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import io.github.daegwonkim.chronicle.repository.LogRepository;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import io.github.daegwonkim.chronicle.vo.LogVo;
import io.github.daegwonkim.chronicle.vo.TimeRangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private LogJdbcRepository logJdbcRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private LogService logService;

    private static final UUID API_KEY = UUID.randomUUID();

    @Test
    @DisplayName("기존 Application이 있으면 해당 ID로 로그를 저장한다")
    void saveLogs_usesExistingApplication() {
        // given
        Project project = Project.create(1L, "Test Project", null);
        Application application = Application.create(project.getId(), "my-app");

        given(projectRepository.findByApiKeyAndDeletedFalse(API_KEY)).willReturn(Optional.of(project));
        given(applicationRepository.findByProjectIdAndNameAndDeletedFalse(project.getId(), "my-app")).willReturn(Optional.of(application));

        SaveLogsDto.Req req = new SaveLogsDto.Req("my-app", List.of(
                new SaveLogsDto.Req.LogEntry(LogLevel.INFO, "test message", "com.example.Main", Instant.now())
        ));

        // when
        logService.saveLogs(API_KEY, req);

        // then
        verify(applicationRepository, never()).saveAndFlush(any());
        verify(logJdbcRepository).saveAll(eq(application.getId()), eq(req.logs()));
    }

    @Test
    @DisplayName("Application이 없으면 새로 생성한 뒤 로그를 저장한다")
    void saveLogs_createsNewApplicationWhenNotFound() {
        // given
        Project project = Project.create(1L, "Test Project", null);
        Application newApplication = Application.create(project.getId(), "new-app");

        given(projectRepository.findByApiKeyAndDeletedFalse(API_KEY)).willReturn(Optional.of(project));
        given(applicationRepository.findByProjectIdAndNameAndDeletedFalse(project.getId(), "new-app")).willReturn(Optional.empty());
        given(applicationRepository.saveAndFlush(any(Application.class))).willReturn(newApplication);

        SaveLogsDto.Req req = new SaveLogsDto.Req("new-app", List.of(
                new SaveLogsDto.Req.LogEntry(LogLevel.ERROR, "error occurred", "com.example.Main", Instant.now())
        ));

        // when
        logService.saveLogs(API_KEY, req);

        // then
        verify(applicationRepository).saveAndFlush(any(Application.class));
        verify(logJdbcRepository).saveAll(eq(newApplication.getId()), eq(req.logs()));
    }

    @Test
    @DisplayName("동시 요청으로 유니크 제약 위반 시 재조회하여 로그를 저장한다")
    void saveLogs_retriesOnDuplicateApplication() {
        // given
        Project project = Project.create(1L, "Test Project", null);
        Application existingApplication = Application.create(project.getId(), "my-app");

        given(projectRepository.findByApiKeyAndDeletedFalse(API_KEY)).willReturn(Optional.of(project));
        given(applicationRepository.findByProjectIdAndNameAndDeletedFalse(project.getId(), "my-app"))
                .willReturn(Optional.empty())
                .willReturn(Optional.of(existingApplication));
        given(applicationRepository.saveAndFlush(any(Application.class))).willThrow(new DataIntegrityViolationException("Duplicate entry"));

        SaveLogsDto.Req req = new SaveLogsDto.Req("my-app", List.of(
                new SaveLogsDto.Req.LogEntry(LogLevel.INFO, "test message", "com.example.Main", Instant.now())
        ));

        // when
        logService.saveLogs(API_KEY, req);

        // then
        verify(logJdbcRepository).saveAll(eq(existingApplication.getId()), eq(req.logs()));
    }

    @Test
    @DisplayName("유효하지 않은 API 키로 요청하면 예외가 발생한다")
    void saveLogs_throwsWhenInvalidApiKey() {
        // given
        UUID invalidApiKey = UUID.randomUUID();
        given(projectRepository.findByApiKeyAndDeletedFalse(invalidApiKey)).willReturn(Optional.empty());

        SaveLogsDto.Req req = new SaveLogsDto.Req("my-app", List.of(
                new SaveLogsDto.Req.LogEntry(LogLevel.INFO, "test", "logger", Instant.now())
        ));

        // when & then
        assertThatThrownBy(() -> logService.saveLogs(invalidApiKey, req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("검색 조건에 맞는 로그 목록과 예상 개수를 반환한다")
    void searchLogs_returnsLogsAndEstimatedCount() {
        // given
        TimeRangeVo timeRange = new TimeRangeVo(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-02T00:00:00Z"));
        SearchLogsDto.Req req = new SearchLogsDto.Req(List.of(1L), timeRange, List.of(LogLevel.ERROR), "NullPointer", null, 10);

        List<LogVo> logs = List.of(
                new LogVo(1L, "my-app", LogLevel.ERROR, "NullPointerException occurred", "com.example.Main", Instant.parse("2025-01-01T12:00:00Z")),
                new LogVo(2L, "my-app", LogLevel.ERROR, "NullPointerException in service", "com.example.Service", Instant.parse("2025-01-01T13:00:00Z"))
        );
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(logs, false));
        given(logRepository.countWithLimit(any(SearchLogsCondition.class), anyInt())).willReturn(2L);

        // when
        SearchLogsDto.Res res = logService.searchLogs(req);

        // then
        assertThat(res.logs()).hasSize(2);
        assertThat(res.hasNext()).isFalse();
        assertThat(res.estimatedCount()).isEqualTo(2L);
        assertThat(res.logs().get(0).message()).isEqualTo("NullPointerException occurred");
    }

    @Test
    @DisplayName("size가 0이면 기본값 20으로 검색한다")
    void searchLogs_usesDefaultSizeWhenZero() {
        // given
        SearchLogsDto.Req req = new SearchLogsDto.Req(List.of(1L), null, null, null, null, 0);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), false));
        given(logRepository.countWithLimit(any(SearchLogsCondition.class), anyInt())).willReturn(0L);

        // when
        logService.searchLogs(req);

        // then
        ArgumentCaptor<SearchLogsCondition> captor = ArgumentCaptor.forClass(SearchLogsCondition.class);
        verify(logRepository).search(captor.capture());

        SearchLogsCondition condition = captor.getValue();
        assertThat(condition.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("size가 0이 아니면 요청한 size를 그대로 사용한다")
    void searchLogs_usesRequestedSize() {
        // given
        SearchLogsDto.Req req = new SearchLogsDto.Req(List.of(1L), null, null, null, null, 50);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), false));
        given(logRepository.countWithLimit(any(SearchLogsCondition.class), anyInt())).willReturn(0L);

        // when
        logService.searchLogs(req);

        // then
        ArgumentCaptor<SearchLogsCondition> captor = ArgumentCaptor.forClass(SearchLogsCondition.class);
        verify(logRepository).search(captor.capture());

        SearchLogsCondition condition = captor.getValue();
        assertThat(condition.size()).isEqualTo(50);
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록과 estimatedCount 0을 반환한다")
    void searchLogs_returnsEmptyWhenNoResults() {
        // given
        SearchLogsDto.Req req = new SearchLogsDto.Req(List.of(1L), null, List.of(LogLevel.TRACE), "nonexistent", null, 10);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), false));
        given(logRepository.countWithLimit(any(SearchLogsCondition.class), anyInt())).willReturn(0L);

        // when
        SearchLogsDto.Res res = logService.searchLogs(req);

        // then
        assertThat(res.logs()).isEmpty();
        assertThat(res.estimatedCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("요청 조건이 SearchLogsCondition으로 올바르게 변환된다")
    void searchLogs_convertsReqToConditionCorrectly() {
        // given
        TimeRangeVo timeRange = new TimeRangeVo(Instant.parse("2025-06-01T00:00:00Z"), Instant.parse("2025-06-30T23:59:59Z"));
        SearchLogsDto.Req req = new SearchLogsDto.Req(List.of(5L), timeRange, List.of(LogLevel.WARN), "timeout", 100L, 30);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), false));
        given(logRepository.countWithLimit(any(SearchLogsCondition.class), anyInt())).willReturn(0L);

        // when
        logService.searchLogs(req);

        // then
        ArgumentCaptor<SearchLogsCondition> captor = ArgumentCaptor.forClass(SearchLogsCondition.class);
        verify(logRepository).search(captor.capture());

        SearchLogsCondition condition = captor.getValue();
        assertThat(condition.appIds()).isEqualTo(List.of(5L));
        assertThat(condition.timeRange()).isEqualTo(timeRange);
        assertThat(condition.logLevels()).isEqualTo(List.of(LogLevel.WARN));
        assertThat(condition.query()).isEqualTo("timeout");
        assertThat(condition.cursorId()).isEqualTo(100L);
        assertThat(condition.size()).isEqualTo(30);
    }
}
