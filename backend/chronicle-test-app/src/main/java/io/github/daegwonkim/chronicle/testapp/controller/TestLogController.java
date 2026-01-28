package io.github.daegwonkim.chronicle.testapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test/logs")
public class TestLogController {

    private static final Logger log = LoggerFactory.getLogger(TestLogController.class);

    @PostMapping
    public Map<String, String> generateLogs() {
        log.trace("TRACE level log message");
        log.debug("DEBUG level log message");
        log.info("INFO level log message");
        log.warn("WARN level log message");
        log.error("ERROR level log message");

        return Map.of("status", "ok", "message", "Generated logs at all levels");
    }

    @PostMapping("/bulk")
    public Map<String, String> generateBulkLogs(@RequestParam(defaultValue = "10") int count) {
        for (int i = 1; i <= count; i++) {
            log.info("Bulk log message {}/{}", i, count);
        }

        return Map.of("status", "ok", "message", "Generated " + count + " log messages");
    }
}
