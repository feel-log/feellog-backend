package com.feellog.backend.domain.catalog.situationtag.entity;

import com.feellog.backend.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "situation_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SituationTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "situation_tag_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}