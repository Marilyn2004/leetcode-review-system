package com.ziyi.leetcodereviewsystem;

import java.util.List;

public record CatalogProblemResponse(Integer id, String platform, String externalProblemId,
        String title, String slug, String difficulty, String url, List<CatalogPatternResponse> patterns) { }
