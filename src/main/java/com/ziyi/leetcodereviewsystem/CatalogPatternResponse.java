package com.ziyi.leetcodereviewsystem;

public record CatalogPatternResponse(Integer id, String slug, String name) {
    static CatalogPatternResponse from(Pattern pattern) {
        return new CatalogPatternResponse(pattern.getId(), pattern.getSlug(), pattern.getName());
    }
}
