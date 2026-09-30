package com.ziyi.leetcodereviewsystem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewSessionRepository extends JpaRepository<ReviewSession, Integer> {
    List<ReviewSession> findByProblemIdOrderByReviewedAtAscIdAsc(Integer problemId);
}
