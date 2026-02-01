package io.github.daegwonkim.chronicle.repository;

import io.github.daegwonkim.chronicle.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    boolean existsByUsernameAndPasswordAndWithdrawnFalse(String username, String password);
    Optional<Admin> findByUsernameAndPasswordAndWithdrawnFalse(String username, String password);
}
