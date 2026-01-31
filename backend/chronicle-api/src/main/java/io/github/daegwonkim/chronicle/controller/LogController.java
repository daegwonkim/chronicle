package io.github.daegwonkim.chronicle.controller;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import io.github.daegwonkim.chronicle.dto.logs.SearchLogsDto;
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
    public void saveLogs(@RequestHeader("X-Api-Key") String apiKey,
                         @RequestBody SaveLogsDto.Req req) {
        logService.saveLogs(apiKey, req);
    }

    @Operation(summary = "로그 조회 API", description = "검색 조건에 맞는 로그를 모두 조회합니다.")
    @GetMapping
    public SearchLogsDto.Res saveLogs(
            @RequestHeader("X-Api-Key") String apiKey,
            @ModelAttribute SearchLogsDto.Req req
    ) {
        return logService.searchLogs(req);
    }
}
