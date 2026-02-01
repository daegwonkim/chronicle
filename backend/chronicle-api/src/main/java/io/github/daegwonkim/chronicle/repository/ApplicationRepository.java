package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByProjectIdAndNameAndDeletedFalse(Long projectId, String name);
    List<Application> findAllByProjectIdOrderByName(Long projectId);
}
