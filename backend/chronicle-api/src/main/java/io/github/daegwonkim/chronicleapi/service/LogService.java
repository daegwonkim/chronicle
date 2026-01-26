package io.github.daegwonkim.chronicleapi.service;

import io.github.daegwonkim.chronicleapi.dto.logs.SaveLogDto;
import io.github.daegwonkim.chronicleapi.entity.Log;
import io.github.daegwonkim.chronicleapi.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    @Transactional
    public void saveLog(SaveLogDto.Req req) {
        Log log = Log.create(req.appId(), req.logLevel(), req.logMessage(), req.loggedAt());
        logRepository.save(log);
    }
}
