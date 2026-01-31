package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SearchLogsDto;
import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import io.github.daegwonkim.chronicle.repository.ApplicationRepository;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import io.github.daegwonkim.chronicle.repository.LogRepository;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogVoServiceTest {

    @Mock
    private LogRepository logRepository;

    @Mock
    private LogJdbcRepository logJdbcRepository;

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private LogService logService;

    @Test
    @DisplayName("검색 조건에 맞는 로그 목록과 총 개수를 반환한다")
    void searchLogs_returnsLogsAndTotalCount() {
        // given
        TimeRangeVo timeRange = new TimeRangeVo(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-02T00:00:00Z"));
        SearchLogsDto.Req req = new SearchLogsDto.Req(1L, timeRange, LogLevel.ERROR, "NullPointer", 0, 10);

        List<LogVo> logs = List.of(
                new LogVo(1L, "my-app", LogLevel.ERROR, "NullPointerException occurred", "com.example.Main", Instant.parse("2025-01-01T12:00:00Z")),
                new LogVo(2L, "my-app", LogLevel.ERROR, "NullPointerException in service", "com.example.Service", Instant.parse("2025-01-01T13:00:00Z"))
        );
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(logs, 2L));

        // when
        SearchLogsDto.Res res = logService.searchLogs(req);

        // then
        assertThat(res.logs()).hasSize(2);
        assertThat(res.totalCount()).isEqualTo(2L);
        assertThat(res.logs().get(0).message()).isEqualTo("NullPointerException occurred");
    }

    @Test
    @DisplayName("size가 0이면 기본값 20으로 검색한다")
    void searchLogs_usesDefaultSizeWhenZero() {
        // given
        SearchLogsDto.Req req = new SearchLogsDto.Req(1L, null, null, null, 0, 0);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), 0L));

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
        SearchLogsDto.Req req = new SearchLogsDto.Req(1L, null, null, null, 0, 50);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), 0L));

        // when
        logService.searchLogs(req);

        // then
        ArgumentCaptor<SearchLogsCondition> captor = ArgumentCaptor.forClass(SearchLogsCondition.class);
        verify(logRepository).search(captor.capture());

        SearchLogsCondition condition = captor.getValue();
        assertThat(condition.size()).isEqualTo(50);
    }

    @Test
    @DisplayName("검색 결과가 없으면 빈 목록과 totalCount 0을 반환한다")
    void searchLogs_returnsEmptyWhenNoResults() {
        // given
        SearchLogsDto.Req req = new SearchLogsDto.Req(1L, null, LogLevel.TRACE, "nonexistent", 0, 10);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), 0L));

        // when
        SearchLogsDto.Res res = logService.searchLogs(req);

        // then
        assertThat(res.logs()).isEmpty();
        assertThat(res.totalCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("요청 조건이 SearchLogsCondition으로 올바르게 변환된다")
    void searchLogs_convertsReqToConditionCorrectly() {
        // given
        TimeRangeVo timeRange = new TimeRangeVo(Instant.parse("2025-06-01T00:00:00Z"), Instant.parse("2025-06-30T23:59:59Z"));
        SearchLogsDto.Req req = new SearchLogsDto.Req(5L, timeRange, LogLevel.WARN, "timeout", 2, 30);
        given(logRepository.search(any(SearchLogsCondition.class))).willReturn(new SearchLogsResult(Collections.emptyList(), 0L));

        // when
        logService.searchLogs(req);

        // then
        ArgumentCaptor<SearchLogsCondition> captor = ArgumentCaptor.forClass(SearchLogsCondition.class);
        verify(logRepository).search(captor.capture());

        SearchLogsCondition condition = captor.getValue();
        assertThat(condition.appId()).isEqualTo(5L);
        assertThat(condition.timeRange()).isEqualTo(timeRange);
        assertThat(condition.logLevel()).isEqualTo(LogLevel.WARN);
        assertThat(condition.query()).isEqualTo("timeout");
        assertThat(condition.page()).isEqualTo(2);
        assertThat(condition.size()).isEqualTo(30);
    }
}
