package com.ziyi.leetcodereviewsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Integer> {
    List<Problem> findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate date);

    long countBySolved(boolean solved);

    long countByNextReviewDateLessThanEqual(LocalDate date);

    @Query("""
            SELECT p FROM Problem p
            WHERE (:difficulty IS NULL OR p.difficulty = :difficulty)
              AND (:pattern IS NULL OR p.pattern = :pattern)
              AND (:solved IS NULL OR p.solved = :solved)
            """)
    List<Problem> findFiltered(
            @Param("difficulty") String difficulty,
            @Param("pattern") String pattern,
            @Param("solved") Boolean solved
    );

    @Query("SELECT COALESCE(SUM(p.timesReviewed), 0) FROM Problem p")
    long sumTimesReviewed();

    @Query("SELECT p.difficulty, COUNT(p) FROM Problem p GROUP BY p.difficulty")
    List<Object[]> countGroupedByDifficulty();

    @Query("SELECT p.pattern, COUNT(p) FROM Problem p GROUP BY p.pattern")
    List<Object[]> countGroupedByPattern();
    @Query("""
            SELECT DISTINCT p FROM Problem p LEFT JOIN FETCH p.patterns
            WHERE p.platform IS NOT NULL AND p.externalProblemId IS NOT NULL
              AND p.slug IS NOT NULL AND p.url IS NOT NULL
              AND (:difficulty IS NULL OR p.difficulty = :difficulty)
              AND (:pattern IS NULL OR EXISTS (
                  SELECT matching.id FROM Problem candidate JOIN candidate.patterns matching
                  WHERE candidate.id = p.id AND matching.slug = :pattern))
            ORDER BY p.id ASC
            """)
    List<Problem> findCatalog(@Param("difficulty") String difficulty, @Param("pattern") String pattern);

    @Query("""
            SELECT p FROM Problem p LEFT JOIN FETCH p.patterns
            WHERE p.id = :id AND p.platform IS NOT NULL AND p.externalProblemId IS NOT NULL
              AND p.slug IS NOT NULL AND p.url IS NOT NULL
            """)
    java.util.Optional<Problem> findCatalogById(@Param("id") Integer id);

    java.util.Optional<Problem> findByPlatformAndExternalProblemId(String platform, String externalProblemId);
    java.util.Optional<Problem> findByPlatformAndSlug(String platform, String slug);
}
