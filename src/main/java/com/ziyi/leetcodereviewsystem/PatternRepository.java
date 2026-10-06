package com.ziyi.leetcodereviewsystem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PatternRepository extends JpaRepository<Pattern, Integer> {
    Optional<Pattern> findBySlug(String slug);
    List<Pattern> findAllByOrderBySlugAsc();
}
