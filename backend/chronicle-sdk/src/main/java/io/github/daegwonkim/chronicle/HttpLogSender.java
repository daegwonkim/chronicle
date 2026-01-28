package io.github.daegwonkim.chronicle;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class HttpLogSender {

    private final String appKey;
    private final String url;
    private final HttpClient httpClient;

    protected HttpLogSender(String appKey, String url) {
        this.appKey = appKey;
        this.url = url;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public void send(LogEntry entry) {
        send(List.of(entry));
    }

    public void send(List<LogEntry> entries) {
        String json = toJson(entries);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("X-App-Key", appKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw new RuntimeException("Failed to send log: " + response.statusCode() + " " + response.body());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send log", e);
        }
    }

    private String toJson(List<LogEntry> entries) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"logs\":[");

        String logsJson = entries.stream()
                .map(this::entryToJson)
                .collect(Collectors.joining(","));

        sb.append(logsJson);
        sb.append("]}");

        return sb.toString();
    }

    private String entryToJson(LogEntry entry) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"level\":\"").append(entry.getLevel()).append("\",");
        sb.append("\"message\":\"").append(escape(entry.getMessage())).append("\",");
        sb.append("\"loggedAt\":\"").append(entry.getLoggedAt()).append("\"");

        if (entry.getLogger() != null) {
            sb.append(",\"logger\":\"").append(escape(entry.getLogger())).append("\"");
        }

        sb.append("}");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
