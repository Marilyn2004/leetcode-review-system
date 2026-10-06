package com.ziyi.leetcodereviewsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Integer> {
    @Query("SELECT r FROM ReviewSession r WHERE r.problem.id = :problemId ORDER BY r.reviewedAt ASC, r.id ASC")
    List<ReviewSession> findByProblemIdOrderByReviewedAtAscIdAsc(@Param("problemId") Integer problemId);
}
