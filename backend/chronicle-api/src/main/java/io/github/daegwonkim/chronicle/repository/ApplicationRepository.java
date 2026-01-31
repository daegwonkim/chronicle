package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByAppKey(UUID appKey);
}
