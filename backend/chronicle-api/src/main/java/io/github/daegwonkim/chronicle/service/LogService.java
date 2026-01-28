package io.github.daegwonkim.chronicle.service;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.entity.Log;
import io.github.daegwonkim.chronicle.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional
    public void saveLogs(SaveLogsDto.Req req) {
        List<Log> logs = req.logs().stream()
                .map(logEntry ->
                        Log.create(
                                logEntry.level(),
                                logEntry.message(),
                                logEntry.logger(),
                                logEntry.loggedAt()
                        )
                )
                .toList();
        logRepository.saveAll(logs);
    }
}
