package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.dto.logs.SearchLogsDto;
import io.github.daegwonkim.chronicle.repository.ApplicationRepository;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import io.github.daegwonkim.chronicle.repository.LogRepository;
import io.github.daegwonkim.chronicle.repository.condition.SearchLogsCondition;
import io.github.daegwonkim.chronicle.repository.result.SearchLogsResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final LogJdbcRepository logJdbcRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public void saveLogs(String appKey, SaveLogsDto.Req req) {
        Long appId = findAppId(appKey);
        logJdbcRepository.saveAll(appId, req.logs());
    }

    @Transactional(readOnly = true)
    public SearchLogsDto.Res searchLogs(SearchLogsDto.Req req) {
        SearchLogsCondition condition = new SearchLogsCondition(
                req.appId(),
                req.timeRange(),
                req.logLevel(),
                req.query(),
                req.page(),
                req.size() != 0 ? req.size() : 20
        );
        SearchLogsResult result = logRepository.search(condition);

        return new SearchLogsDto.Res(result.logs(), result.totalCount());
    }

    private Long findAppId(String appKey) {
        return applicationRepository.findByAppKey(UUID.fromString(appKey))
                .orElseThrow(() -> new IllegalArgumentException("Invalid app key: " + appKey))
                .getId();
    }
}