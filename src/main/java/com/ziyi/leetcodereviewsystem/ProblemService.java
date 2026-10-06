package com.ziyi.leetcodereviewsystem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProblemService {
    private final ProblemRepository problemRepository;
    private final ReviewSessionRepository reviewSessionRepository;

    public ProblemService(ProblemRepository problemRepository, ReviewSessionRepository reviewSessionRepository) {
        this.problemRepository = problemRepository;
        this.reviewSessionRepository = reviewSessionRepository;
    }

    public List<Problem> getProblems(String difficulty, String pattern, Boolean solved) {
        return problemRepository.findFiltered(difficulty, pattern, solved);
    }

    public Problem addProblem(Problem problem) {
        // Never merge a request entity, even when a client supplies an existing ID.
        Problem created = new Problem(null, problem.getTitle(), problem.getDifficulty(),
                problem.getPattern(), problem.getNotes(), problem.getTimesReviewed(),
                problem.isSolved(), problem.getLastReviewed(), problem.getNextReviewDate());
        return problemRepository.save(created);
    }

    public Problem findProblemById(Integer id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException(id));
    }

    public void deleteProblemById(Integer id) {
        if (!problemRepository.existsById(id)) {
            throw new ProblemNotFoundException(id);
        }
        problemRepository.deleteById(id);
    }

    public Problem updateProblem(Integer id, Problem updatedProblem) {
        Problem existing = findProblemById(id);
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

    public Problem markReviewed(Integer id) {
        Problem problem = findProblemById(id);
        problem.markReviewed();
        Problem saved = problemRepository.save(problem);
        reviewSessionRepository.save(new ReviewSession(saved, LocalDateTime.now(), saved.getNextReviewDate()));
        return saved;
    }

    public List<ReviewSession> getReviewHistory(Integer id) {
        findProblemById(id);
        return reviewSessionRepository.findByProblemIdOrderByReviewedAtAscIdAsc(id);
    }

    public List<Problem> getDueProblems() {
        return problemRepository.findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(LocalDate.now());
    }

    public StatsResponse getStats() {
        LocalDate today = LocalDate.now();
        Map<String, Long> difficultyBreakdown = new LinkedHashMap<>();
        difficultyBreakdown.put("Easy", 0L);
        difficultyBreakdown.put("Medium", 0L);
        difficultyBreakdown.put("Hard", 0L);
        putCounts(difficultyBreakdown, problemRepository.countGroupedByDifficulty());

        Map<String, Long> patternBreakdown = new LinkedHashMap<>();
        putCounts(patternBreakdown, problemRepository.countGroupedByPattern());

        return new StatsResponse(
                problemRepository.count(),
                problemRepository.countBySolved(true),
                problemRepository.countByNextReviewDateLessThanEqual(today),
                problemRepository.sumTimesReviewed(),
                difficultyBreakdown,
                patternBreakdown
        );
    }

    private static void putCounts(Map<String, Long> target, List<Object[]> rows) {
        for (Object[] row : rows) {
            if (row[0] == null) {
                continue;
            }
            target.put((String) row[0], ((Number) row[1]).longValue());
        }
    }
}
