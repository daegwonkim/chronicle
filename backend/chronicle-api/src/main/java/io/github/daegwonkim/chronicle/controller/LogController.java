package io.github.daegwonkim.chronicle.controller;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @Operation(summary = "로그 저장 API", description = "로그를 저장합니다.")
    @PostMapping
    public void saveLogs(@RequestHeader("X-App-Key") String appKey,
                         @RequestBody SaveLogsDto.Req req) {
        logService.saveLogs(appKey, req);
    }
}
