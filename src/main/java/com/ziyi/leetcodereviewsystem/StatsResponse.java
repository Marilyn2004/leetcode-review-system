package com.ziyi.leetcodereviewsystem;

import java.util.Map;

public record StatsResponse(
        long totalProblems,
        long solvedProblems,
        long dueProblems,
        long totalReviews,
        Map<String, Long> difficultyBreakdown,
        Map<String, Long> patternBreakdown
) {
}
