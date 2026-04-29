package com.feellog.backend.domain.catalog.category.entity;

import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoryGroup extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_group_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @OneToMany(mappedBy = "categoryGroup")
    private List<Category> categories = new ArrayList<>();
}