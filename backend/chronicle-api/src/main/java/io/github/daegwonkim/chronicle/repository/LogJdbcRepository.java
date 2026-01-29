package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.dto.logs.SaveLogsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LogJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveAll(Long appId, List<SaveLogsDto.Req.LogEntry> logEntries) {
        String sql = "INSERT INTO logs (app_id, level, message, logger, logged_at) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                SaveLogsDto.Req.LogEntry entry = logEntries.get(i);
                ps.setLong(1, appId);
                ps.setString(2, entry.level().name());
                ps.setString(3, entry.message());
                ps.setString(4, entry.logger());
                ps.setTimestamp(5, Timestamp.from(entry.loggedAt()));
            }

            @Override
            public int getBatchSize() {
                return logEntries.size();
            }
        });
    }
}
