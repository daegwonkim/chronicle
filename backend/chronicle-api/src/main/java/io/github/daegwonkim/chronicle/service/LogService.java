package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.dto.logs.SearchLogsDto;
import io.github.daegwonkim.chronicle.entity.Application;
import io.github.daegwonkim.chronicle.entity.Project;
import io.github.daegwonkim.chronicle.repository.ApplicationRepository;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import io.github.daegwonkim.chronicle.repository.LogRepository;
import io.github.daegwonkim.chronicle.repository.ProjectRepository;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final LogJdbcRepository logJdbcRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;

    private final static int COUNT_LOG_LIMIT = 10_001;

    @Transactional
    public void saveLogs(UUID apiKey, SaveLogsDto.Req req) {
        Project project = projectRepository.findByApiKeyAndDeletedFalse(apiKey)
                .orElseThrow(() -> new IllegalArgumentException("Invalid api key: " + apiKey));
        Application application = applicationRepository.findByProjectIdAndNameAndDeletedFalse(project.getId(), req.appName())
                .orElseGet(() -> {
                    try {
                        return applicationRepository.saveAndFlush(Application.create(project.getId(), req.appName()));
                    } catch (DataIntegrityViolationException e) {
                        return applicationRepository.findByProjectIdAndNameAndDeletedFalse(project.getId(), req.appName())
                                .orElseThrow();
                    }
                });

        logJdbcRepository.saveAll(application.getId(), req.logs());
    }

    @Transactional(readOnly = true)
    public SearchLogsDto.Res searchLogs(SearchLogsDto.Req req) {
        SearchLogsCondition condition = new SearchLogsCondition(
                req.appIds(),
                req.timeRange(),
                req.logLevels(),
                req.query(),
                req.cursorId(),
                req.size() != 0 ? req.size() : 20
        );

        SearchLogsResult searchResult = logRepository.search(condition);
        long estimatedCount = logRepository.countWithLimit(condition, COUNT_LOG_LIMIT);

        return new SearchLogsDto.Res(searchResult.logs(), searchResult.hasNext(), estimatedCount);
    }
}