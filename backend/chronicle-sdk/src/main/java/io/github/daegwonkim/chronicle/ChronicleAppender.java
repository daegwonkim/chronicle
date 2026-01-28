package io.github.daegwonkim.chronicle;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ChronicleAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String appKey;
    private String url;
    private int batchSize = 100;
    private int bufferSize = 5000;
    private long flushIntervalMs = 5000;

    private HttpLogSender logSender;
    private BlockingQueue<LogEntry> buffer;
    private ScheduledExecutorService scheduler;

    protected BlockingQueue<LogEntry> getBuffer() {
        return this.buffer;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setFlushIntervalMs(long flushIntervalMs) {
        this.flushIntervalMs = flushIntervalMs;
    }

    protected void setLogSender(HttpLogSender logSender) {
        this.logSender = logSender;
    }

    @Override
    public void start() {
        if (appKey == null || url == null) {
            addError("appKey and url are required");
            return;
        }

        logSender = new HttpLogSender(appKey, url);
        buffer = new LinkedBlockingQueue<>(bufferSize);
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "chronicle-flusher");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(this::flush, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);

        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        LogEntry entry = toLogEntry(event);
        if (!buffer.offer(entry)) {
            addWarn("Chronicle buffer full, dropping log entry");
            return;
        }

        if (buffer.size() >= batchSize) {
            scheduler.execute(this::flush);
        }
    }

    @Override
    public void stop() {
        super.stop();
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                scheduler.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        flush();
    }

    private void flush() {
        if (buffer.isEmpty()) {
            return;
        }

        List<LogEntry> batch = new ArrayList<>();
        buffer.drainTo(batch);

        if (batch.isEmpty()) {
            return;
        }

        try {
            logSender.send(batch);
        } catch (Exception e) {
            addWarn("Failed to send logs to Chronicle server", e);
        }
    }

    private LogEntry toLogEntry(ILoggingEvent event) {
        LogLevel level = toLogLevel(event.getLevel());

        LogEntry.Builder builder = LogEntry.builder(level)
                .message(event.getFormattedMessage())
                .logger(event.getLoggerName())
                .loggedAt(Instant.ofEpochMilli(event.getTimeStamp()));

        return builder.build();
    }

    private LogLevel toLogLevel(Level level) {
        return switch (level.toInt()) {
            case Level.TRACE_INT -> LogLevel.TRACE;
            case Level.DEBUG_INT -> LogLevel.DEBUG;
            case Level.WARN_INT -> LogLevel.WARN;
            case Level.ERROR_INT -> LogLevel.ERROR;
            default -> LogLevel.INFO;
        };
    }
}
