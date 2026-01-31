package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByAppKey(UUID appKey);
}
