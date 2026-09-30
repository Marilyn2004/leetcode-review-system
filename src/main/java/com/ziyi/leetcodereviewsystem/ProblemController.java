package com.ziyi.leetcodereviewsystem;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProblemController {
    private final ProblemService problemService;
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping("/problems")
    public List<Problem> getAllProblems() {
        return problemService.getAllProblems();
    }

    @PostMapping("/problems")
    public Problem addProblem(@RequestBody Problem problem) {
        return problemService.addProblem(problem);
    }

    @GetMapping("/problems/{id}")
    public Problem getProblemById(@PathVariable Integer id) {
        return problemService.findProblemById(id);
    }

    @DeleteMapping("/problems/{id}")
    public boolean deleteProblem(@PathVariable Integer id) {
        return problemService.deleteProblemById(id);
    }

    @PutMapping("/problems/{id}")
    public Problem updateProblem(@PathVariable Integer id, @RequestBody Problem problem) {
        return problemService.updateProblem(id, problem);
    }

}
