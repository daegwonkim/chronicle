package io.github.daegwonkim.chronicle;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.status.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChronicleAppenderTest {

    @Mock
    private HttpLogSender logSender;
    private ChronicleAppender appender;

    @BeforeEach
    void setUp() {
        appender = new ChronicleAppender();

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getStatusManager().clear();

        appender.setContext(context);
        appender.setAppKey("test-key");
        appender.setUrl("http://localhost:8080");
        appender.setBatchSize(5);
        appender.setBufferSize(10);
        appender.setFlushIntervalMs(1000);

        appender.start();
        appender.setLogSender(logSender);
    }

    @AfterEach
    void tearDown() {
        appender.stop();
    }

    @Test
    @DisplayName("로그가 배치 사이즈만큼 쌓이면 자동으로 전송되어야 한다")
    void shouldFlushWhenBatchSizeReached() {
        // given
        for (int i = 0; i < 5; i++) {
            ILoggingEvent event = createMockEvent("Log Message " + i);

            // when
            appender.append(event);
        }

        // then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(logSender, atLeastOnce()).send(anyList());
        });
    }

    @Test
    @DisplayName("배치 사이즈가 차지 않아도 설정한 시간이 지나면 전송되어야 한다")
    void shouldFlushAfterInterval() {
        appender.append(createMockEvent("Single Log"));

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(logSender, atLeastOnce()).send(anyList());
        });
    }

    @Test
    @DisplayName("버퍼가 가득 차면 로그를 드랍해야 한다")
    void shouldDropLogWhenBufferIsFull() {
        appender.setBatchSize(10);
        appender.setBufferSize(5);

        appender.start();
        appender.setLogSender(logSender);

        // given
        for (int i = 0; i < 5; i++) {
            ILoggingEvent event = createMockEvent("Log Message " + i);

            // when
            appender.append(event);
        }

        ILoggingEvent droppedEvent = createMockEvent("Dropped Log");
        appender.append(droppedEvent);

        List<Status> statusList = appender.getStatusManager().getCopyOfStatusList();
        boolean hasDropWarn = statusList.stream()
                .anyMatch(s -> s.getMessage().contains("Chronicle buffer full, dropping log entry"));

        // then
        assertThat(appender.getBuffer().size()).isEqualTo(5);
        assertThat(hasDropWarn).isTrue();
        assertThat(appender.getBuffer())
                .extracting(LogEntry::getMessage)
                .doesNotContain("Dropped Log");
    }

    @Test
    @DisplayName("서버 전송 중 예외가 발생해도 애플리케이션에 영향을 주지 않아야 한다")
    void shouldHandleSenderExceptionGracefully() {
        appender.start();
        appender.setLogSender(logSender);

        // given
        doThrow(new RuntimeException("Server Down")).when(logSender).send(anyList());

        // when
        for (int i = 0; i < 5; i++) {
            appender.append(createMockEvent("Log " + i));
        }

        // then
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Status> statusList = appender.getStatusManager().getCopyOfStatusList();
            boolean hasFailWarn = statusList.stream()
                    .anyMatch(s -> s.getMessage().contains("Failed to send logs"));
            assertThat(hasFailWarn).isTrue();
        });
    }

    @Test
    @DisplayName("여러 스레드에서 동시에 로그를 던져도 유실 없이 전송되어야 한다")
    void shouldHandleConcurrentLogAppend() throws InterruptedException {
        int threadCount = 10;
        int logsPerThread = 100;
        int totalLogs = threadCount * logsPerThread;

        appender.setBatchSize(50);
        appender.setBufferSize(1000);
        appender.start();
        appender.setLogSender(logSender);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < logsPerThread; j++) {
                        appender.append(createMockEvent("Concurrent Log"));
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        finishLatch.await();

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ArgumentCaptor<List<LogEntry>> captor = ArgumentCaptor.forClass(List.class);
            verify(logSender, atLeastOnce()).send(captor.capture());

            long processedLogs = captor.getAllValues().stream()
                    .mapToLong(List::size)
                    .sum();

            assertThat(processedLogs).isEqualTo(totalLogs);
        });

        executorService.shutdown();
    }

    private ILoggingEvent createMockEvent(String message) {
        ILoggingEvent event = mock(ILoggingEvent.class);
        when(event.getLevel()).thenReturn(Level.INFO);
        when(event.getFormattedMessage()).thenReturn(message);
        when(event.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(event.getLoggerName()).thenReturn("TestLogger");
        return event;
    }
}
