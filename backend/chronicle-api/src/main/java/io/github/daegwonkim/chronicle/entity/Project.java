package io.github.daegwonkim.chronicle.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false, updatable = false)
    private Long adminId;

    @Column(name = "api_key", unique = true, nullable = false, updatable = false)
    private UUID apiKey;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @LastModifiedDate
    @Column(name = "modified_at", nullable = false)
    private Instant modifiedAt = Instant.now();

    public void modify(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void delete() {
        this.deleted = true;
    }

    public static Project create(Long adminId, String name, String description) {
        Project project = new Project();

        project.adminId = adminId;
        project.apiKey = UUID.randomUUID();
        project.name = name;
        project.description = description;

        return project;
    }
}
