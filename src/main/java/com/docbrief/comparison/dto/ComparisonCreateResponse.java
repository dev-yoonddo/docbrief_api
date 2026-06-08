package com.docbrief.comparison.dto;

import java.util.List;

public record ComparisonCreateResponse(
        Long jobId,
        List<Long> documentIds
) {}
