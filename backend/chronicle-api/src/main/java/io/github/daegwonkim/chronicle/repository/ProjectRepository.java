package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByAdminId(Long adminId);
    Optional<Project> findByApiKeyAndDeletedFalse(String apiKey);
}
