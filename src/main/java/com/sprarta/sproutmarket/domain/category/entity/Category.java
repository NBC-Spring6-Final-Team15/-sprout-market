package com.sprarta.sproutmarket.domain.category.entity;

import com.sprarta.sproutmarket.domain.common.entity.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Unique
    @NotNull
    private String name;

    @Enumerated(EnumType.STRING)
    private Status activeStatus = Status.ACTIVE;

    public void update(String name) {
        this.name = name;
    }

    public void deactivate() {
        this.activeStatus = Status.DELETED;
    }

    public Category(String name) {
        this.name = name;
    }
}
