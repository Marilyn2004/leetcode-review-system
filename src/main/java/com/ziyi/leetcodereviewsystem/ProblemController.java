package com.ziyi.leetcodereviewsystem;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProblemController {
    private final ProblemService problemService;
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/problems")
    public List<Problem> getAllProblems(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String pattern,
            @RequestParam(required = false) Boolean solved
    ) {
        return problemService.getProblems(difficulty, pattern, solved);
    }

    @PostMapping("/problems")
    public Problem addProblem(@Valid @RequestBody Problem problem) {
        return problemService.addProblem(problem);
    }

    @GetMapping("/problems/{id}")
    public Problem getProblemById(@PathVariable Integer id) {
        return problemService.findProblemById(id);
    }

    @DeleteMapping("/problems/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProblem(@PathVariable Integer id) {
        problemService.deleteProblemById(id);
    }

    @PutMapping("/problems/{id}")
    public Problem updateProblem(@PathVariable Integer id, @Valid @RequestBody Problem problem) {
        return problemService.updateProblem(id, problem);
    }

    @PostMapping("/problems/{id}/review")
    public Problem markReviewed(@PathVariable Integer id) {
        return problemService.markReviewed(id);
    }

    @GetMapping("/problems/{id}/reviews")
    public List<ReviewSession> getReviewHistory(@PathVariable Integer id) {
        return problemService.getReviewHistory(id);
    }

    @GetMapping("/problems/due")
    public List<Problem> getDueProblems() {
        return problemService.getDueProblems();
    }

}
