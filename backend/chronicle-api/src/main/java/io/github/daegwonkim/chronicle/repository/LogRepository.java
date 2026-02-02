package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long>, CustomLogRepository {
}
