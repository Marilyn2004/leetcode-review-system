package com.ziyi.leetcodereviewsystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {
    private final ProblemService problemService;

    public StatsController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/stats")
    public StatsResponse getStats() {
        return problemService.getStats();
    }
}
