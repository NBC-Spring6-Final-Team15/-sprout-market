package com.sprarta.sproutmarket.domain.category.entity;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public void update(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.status = Status.DELETED;
    }

    public Category(String name) {
        this.name = name;
    }
}
