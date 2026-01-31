package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.repository.ApplicationRepository;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogJdbcRepository logJdbcRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    public void saveLogs(String appKey, SaveLogsDto.Req req) {
        Long appId = findAppId(appKey);
        logJdbcRepository.saveAll(appId, req.logs());
    }

    private Long findAppId(String appKey) {
        return applicationRepository.findByAppKey(UUID.fromString(appKey))
                .orElseThrow(() -> new IllegalArgumentException("Invalid app key: " + appKey))
                .getId();
    }
}