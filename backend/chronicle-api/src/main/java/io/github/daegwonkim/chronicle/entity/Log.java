package io.github.daegwonkim.chronicle.entity;

import io.github.daegwonkim.chronicle.enumerate.LogLevel;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "logs", indexes = @Index(name = "idx_logs_app_id_level", columnList = "app_id, level"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    @Column(nullable = false, updatable = false)
    private String logger;

    @Column(name = "logged_at", nullable = false, updatable = false)
    private Instant loggedAt;
}
