package com.ziyi.leetcodereviewsystem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProblemService {
    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Problem addProblem(Problem problem) {
        return problemRepository.save(problem);
    }

    public Problem findProblemById(Integer id) {
        return problemRepository.findById(id).orElse(null);
    }

    public boolean deleteProblemById(Integer id) {
        if (!problemRepository.existsById(id)) {
            return false;
        }
        problemRepository.deleteById(id);
        return true;
    }

    public Problem updateProblem(Integer id, Problem updatedProblem) {
        Problem existing = findProblemById(id);
        if (existing == null) {
            return null;
        }
        existing.setTitle(updatedProblem.getTitle());
        existing.setDifficulty(updatedProblem.getDifficulty());
        existing.setPattern(updatedProblem.getPattern());
        existing.setNotes(updatedProblem.getNotes());
        existing.setTimesReviewed(updatedProblem.getTimesReviewed());
        existing.setSolved(updatedProblem.isSolved());
        existing.setLastReviewed(updatedProblem.getLastReviewed());
        existing.setNextReviewDate(updatedProblem.getNextReviewDate());
        return problemRepository.save(existing);
    }

}
