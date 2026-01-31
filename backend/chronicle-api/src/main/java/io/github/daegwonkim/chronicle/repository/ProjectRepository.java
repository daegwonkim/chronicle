package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByAdminId(Long adminId);
}
