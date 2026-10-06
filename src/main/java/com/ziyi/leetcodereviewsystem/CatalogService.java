package com.ziyi.leetcodereviewsystem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CatalogService {
    private final ProblemRepository problems;
    private final PatternRepository patterns;
    public CatalogService(ProblemRepository problems, PatternRepository patterns) {
        this.problems = problems;
        this.patterns = patterns;
    }
    public List<CatalogProblemResponse> list(String difficulty, String pattern) {
        return problems.findCatalog(difficulty, pattern).stream().map(this::response).toList();
    }
    public CatalogProblemResponse get(Integer id) {
        return response(problems.findCatalogById(id).orElseThrow(() -> new ProblemNotFoundException(id)));
    }
    public List<CatalogPatternResponse> patterns() {
        return patterns.findAllByOrderBySlugAsc().stream().map(CatalogPatternResponse::from).toList();
    }
    private CatalogProblemResponse response(Problem p) {
        return new CatalogProblemResponse(p.getId(), p.getPlatform(), p.getExternalProblemId(),
                p.getTitle(), p.getSlug(), p.getDifficulty(), p.getUrl(), p.getPatterns().stream()
                .sorted(Comparator.comparing(Pattern::getSlug)).map(CatalogPatternResponse::from).toList());
    }
}
