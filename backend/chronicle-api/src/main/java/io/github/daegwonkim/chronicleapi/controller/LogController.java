package io.github.daegwonkim.chronicleapi.controller;

import io.github.daegwonkim.chronicleapi.dto.logs.SaveLogDto;
import io.github.daegwonkim.chronicleapi.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public void saveLog(SaveLogDto.Req req) {
        logService.saveLog(req);
    }
}
