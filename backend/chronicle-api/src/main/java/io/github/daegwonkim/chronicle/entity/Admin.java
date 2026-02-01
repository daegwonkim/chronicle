package io.github.daegwonkim.chronicle.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "admins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Admin {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean withdrawn = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public static Admin create(String username, String password) {
        Admin admin = new Admin();

        admin.username = username;
        admin.password = password;

        return admin;
    }
}
