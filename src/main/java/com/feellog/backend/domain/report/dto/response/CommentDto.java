package com.feellog.backend.domain.report.dto.response;

import lombok.Builder;

@Builder
public record CommentDto(
        String type,
        String targetName,
        String message
) {}
