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
}
