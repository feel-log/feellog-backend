package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

@Builder
public record SituationStatDto(
        Long situationTagId,
        String situationName,
        Integer occurrenceCount,
        Integer rank
) {}
