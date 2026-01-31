package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
