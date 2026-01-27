package io.github.daegwonkim.chronicle;

import java.time.Instant;

public class LogEntry {

    private final LogLevel level;
    private final String message;
    private final String logger;
    private final Instant loggedAt;

    private LogEntry(Builder builder) {
        this.level = builder.level;
        this.message = builder.message;
        this.logger = builder.logger;
        this.loggedAt = builder.loggedAt != null ? builder.loggedAt : Instant.now();
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getLogger() {
        return logger;
    }

    public Instant getLoggedAt() {
        return loggedAt;
    }

    public static Builder builder(LogLevel level) {
        return new Builder(level);
    }

    public static class Builder {
        private LogLevel level;
        private String message;
        private String logger;
        private Instant loggedAt;

        private Builder(LogLevel level) {
            this.level = level;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder logger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder loggedAt(Instant loggedAt) {
            this.loggedAt = loggedAt;
            return this;
        }

        public LogEntry build() {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("message is required");
            }
            return new LogEntry(this);
        }
    }
}
