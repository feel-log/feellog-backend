package com.feellog.backend.domain.review.repository;

import com.feellog.backend.domain.situationtag.entity.SituationTag;
import com.feellog.backend.domain.review.dto.SituationTagOptionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SituationTagRepository extends JpaRepository<SituationTag, Long> {

    @Query("""
        SELECT new com.feellog.backend.domain.review.dto.SituationTagOptionResponse(
            s.id,
            s.name
        )
        FROM SituationTag s
        ORDER BY s.id ASC
    """)
    List<SituationTagOptionResponse> findSituationTagOptions();
}