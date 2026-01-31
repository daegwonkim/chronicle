package io.github.daegwonkim.chronicle.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Application {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app_key", unique = true, nullable = false, updatable = false)
    private UUID appKey;

    @Column(name = "project_id", nullable = false, updatable = false)
    private Long projectId;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Boolean deleted = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
