package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long>, CustomProjectRepository {
    Optional<Project> findByApiKeyAndDeletedFalse(UUID apiKey);
}
