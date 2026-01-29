package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.repository.LogJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogJdbcRepository logJdbcRepository;

    public void saveLogs(SaveLogsDto.Req req) {
        logJdbcRepository.saveAll(0L, req.logs());
    }
}