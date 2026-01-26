package io.github.daegwonkim.chronicleapi.repository;

import io.github.daegwonkim.chronicleapi.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {}
