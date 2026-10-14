package com.bank.webframwork.user.common.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant lastUpdated;

    @Column(nullable = false)
    private boolean deleted = false; // soft delete 용도

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        created = now;
        lastUpdated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = Instant.now();
    }

    @PreRemove
    protected void preventHardDelete() {
        throw new IllegalAccessError("Use softDelete instead of remove/delete."); // hard delete 방지
    }

    public void softDelete() {
        deleted = true;
    }
}
