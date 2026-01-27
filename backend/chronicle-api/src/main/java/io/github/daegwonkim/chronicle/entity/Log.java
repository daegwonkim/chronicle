package io.github.daegwonkim.chronicle.entity;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "logs")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_id", nullable = false, updatable = false)
    private Long appId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private LogLevel level;

    @Column(updatable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private Instant loggedAt;

    public static Log create(Long appId, LogLevel level, String message, Instant loggedAt) {
        Log log = new Log();

        log.appId = appId;
        log.level = level;
        log.message = message;
        log.loggedAt = loggedAt;

        return log;
    }
}
