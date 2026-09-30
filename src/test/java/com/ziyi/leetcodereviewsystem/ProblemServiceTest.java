package com.ziyi.leetcodereviewsystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

    @Mock
    private ReviewSessionRepository reviewSessionRepository;

    @InjectMocks
    private ProblemService problemService;

    @Test
    void updateProblemReplacesFieldsAndKeepsPathId() {
        Problem existing = new Problem(
                1,
                "Two Sum",
                "Easy",
                "HashMap",
                "Use complement lookup",
                0,
                true,
                null,
                LocalDate.now()
        );
        Problem update = new Problem(
                99,
                "Updated Two Sum",
                "Medium",
                "HashMap",
                "New notes",
                2,
                false,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 8)
        );

        when(problemRepository.findById(1)).thenReturn(Optional.of(existing));
        when(problemRepository.save(any(Problem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Problem result = problemService.updateProblem(1, update);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Updated Two Sum", result.getTitle());
        assertEquals("Medium", result.getDifficulty());
        assertEquals("New notes", result.getNotes());
        assertEquals(2, result.getTimesReviewed());
        assertFalse(result.isSolved());
        assertEquals(LocalDate.of(2026, 1, 1), result.getLastReviewed());
        assertEquals(LocalDate.of(2026, 1, 8), result.getNextReviewDate());
        verify(problemRepository).save(existing);
    }

    @Test
    void updateProblemThrowsWhenIdIsMissing() {
        when(problemRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ProblemNotFoundException.class, () -> problemService.updateProblem(999, new Problem()));
        verify(problemRepository, never()).save(any());
    }

    @Test
    void deleteProblemByIdRemovesExistingProblem() {
        when(problemRepository.existsById(1)).thenReturn(true);

        problemService.deleteProblemById(1);
        verify(problemRepository).deleteById(1);
    }

    @Test
    void deleteProblemByIdThrowsWhenIdIsMissing() {
        when(problemRepository.existsById(999)).thenReturn(false);

        assertThrows(ProblemNotFoundException.class, () -> problemService.deleteProblemById(999));
        verify(problemRepository, never()).deleteById(any());
    }

    @Test
    void markReviewedUpdatesScheduleAndSavesReviewSession() {
        Problem existing = new Problem(
                1,
                "Two Sum",
                "Easy",
                "HashMap",
                "Use complement lookup",
                1,
                true,
                LocalDate.now().minusDays(1),
                LocalDate.now()
        );

        when(problemRepository.findById(1)).thenReturn(Optional.of(existing));
        when(problemRepository.save(any(Problem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(reviewSessionRepository.save(any(ReviewSession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Problem result = problemService.markReviewed(1);

        LocalDate today = LocalDate.now();
        assertNotNull(result);
        assertEquals(2, result.getTimesReviewed());
        assertEquals(today, result.getLastReviewed());
        assertEquals(today.plusDays(3), result.getNextReviewDate());
        verify(problemRepository).save(existing);

        ArgumentCaptor<ReviewSession> sessionCaptor = ArgumentCaptor.forClass(ReviewSession.class);
        verify(reviewSessionRepository).save(sessionCaptor.capture());
        ReviewSession session = sessionCaptor.getValue();
        assertEquals(existing, session.getProblem());
        assertEquals(1, session.getProblemId());
        assertEquals(today.plusDays(3), session.getNextReviewDate());
        assertFalse(session.getReviewedAt().isAfter(LocalDateTime.now().plusSeconds(2)));
        assertEquals(today, session.getReviewedAt().toLocalDate());
    }

    @Test
    void markReviewedThrowsWhenIdIsMissing() {
        when(problemRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProblemNotFoundException.class, () -> problemService.markReviewed(999));
        verify(problemRepository, never()).save(any());
        verify(reviewSessionRepository, never()).save(any());
    }

    @Test
    void getDueProblemsUsesRepositoryOrderingByNextReviewDate() {
        LocalDate today = LocalDate.now();
        Problem mostOverdue = new Problem(1, "Oldest", "Easy", "Array", "notes", 1, true, today.minusDays(5), today.minusDays(4));
        Problem dueToday = new Problem(2, "Today", "Medium", "HashMap", "notes", 1, true, today.minusDays(1), today);
        when(problemRepository.findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(today))
                .thenReturn(List.of(mostOverdue, dueToday));

        List<Problem> result = problemService.getDueProblems();

        assertEquals(List.of(1, 2), result.stream().map(Problem::getId).toList());
        verify(problemRepository).findByNextReviewDateLessThanEqualOrderByNextReviewDateAsc(today);
    }

    @Test
    void getProblemsWithoutFiltersDelegatesToRepository() {
        Problem problem = new Problem(1, "Two Sum", "Easy", "HashMap", "notes", 0, false, null, null);
        when(problemRepository.findFiltered(null, null, null)).thenReturn(List.of(problem));

        List<Problem> result = problemService.getProblems(null, null, null);

        assertEquals(1, result.size());
        verify(problemRepository).findFiltered(null, null, null);
    }

    @Test
    void getProblemsFiltersByDifficulty() {
        when(problemRepository.findFiltered("Hard", null, null)).thenReturn(List.of(
                new Problem(1, "Hard One", "Hard", "DP", "notes", 0, false, null, null)
        ));

        List<Problem> result = problemService.getProblems("Hard", null, null);

        assertEquals(1, result.size());
        assertEquals("Hard", result.get(0).getDifficulty());
        verify(problemRepository).findFiltered("Hard", null, null);
    }

    @Test
    void getProblemsFiltersByPattern() {
        when(problemRepository.findFiltered(null, "Two Pointers", null)).thenReturn(List.of(
                new Problem(1, "Container", "Medium", "Two Pointers", "notes", 0, true, null, null)
        ));

        List<Problem> result = problemService.getProblems(null, "Two Pointers", null);

        assertEquals(1, result.size());
        assertEquals("Two Pointers", result.get(0).getPattern());
        verify(problemRepository).findFiltered(null, "Two Pointers", null);
    }

    @Test
    void getProblemsFiltersBySolved() {
        when(problemRepository.findFiltered(null, null, true)).thenReturn(List.of(
                new Problem(1, "Solved", "Easy", "Array", "notes", 1, true, LocalDate.now(), LocalDate.now())
        ));

        List<Problem> result = problemService.getProblems(null, null, true);

        assertEquals(1, result.size());
        assertTrue(result.get(0).isSolved());
        verify(problemRepository).findFiltered(null, null, true);
    }

    @Test
    void getProblemsAppliesCombinedFilters() {
        when(problemRepository.findFiltered("Medium", "Two Pointers", true)).thenReturn(List.of(
                new Problem(3, "Combined", "Medium", "Two Pointers", "notes", 2, true, LocalDate.now(), LocalDate.now())
        ));

        List<Problem> result = problemService.getProblems("Medium", "Two Pointers", true);

        assertEquals(1, result.size());
        assertEquals("Medium", result.getFirst().getDifficulty());
        assertEquals("Two Pointers", result.getFirst().getPattern());
        assertTrue(result.getFirst().isSolved());
        verify(problemRepository).findFiltered("Medium", "Two Pointers", true);
    }

    @Test
    void getStatsAggregatesCountsAndBreakdowns() {
        when(problemRepository.count()).thenReturn(5L);
        when(problemRepository.countBySolved(true)).thenReturn(3L);
        when(problemRepository.countByNextReviewDateLessThanEqual(LocalDate.now())).thenReturn(2L);
        when(problemRepository.sumTimesReviewed()).thenReturn(12L);
        when(problemRepository.countGroupedByDifficulty()).thenReturn(List.of(
                new Object[]{"Easy", 2L},
                new Object[]{"Hard", 3L}
        ));
        when(problemRepository.countGroupedByPattern()).thenReturn(List.of(
                new Object[]{"Arrays", 4L},
                new Object[]{"Two Pointers", 1L}
        ));

        StatsResponse stats = problemService.getStats();

        assertEquals(5, stats.totalProblems());
        assertEquals(3, stats.solvedProblems());
        assertEquals(2, stats.dueProblems());
        assertEquals(12, stats.totalReviews());
        assertEquals(2, stats.difficultyBreakdown().get("Easy"));
        assertEquals(0, stats.difficultyBreakdown().get("Medium"));
        assertEquals(3, stats.difficultyBreakdown().get("Hard"));
        assertEquals(4, stats.patternBreakdown().get("Arrays"));
        assertEquals(1, stats.patternBreakdown().get("Two Pointers"));
    }

    @Test
    void getReviewHistoryReturnsChronologicalSessions() {
        Problem problem = new Problem(1, "Two Sum", "Easy", "HashMap", "notes", 2, true, LocalDate.now(), LocalDate.now());
        ReviewSession first = new ReviewSession(problem, LocalDateTime.now().minusDays(3), LocalDate.now().minusDays(2));
        ReviewSession second = new ReviewSession(problem, LocalDateTime.now().minusDays(1), LocalDate.now());
        when(problemRepository.findById(1)).thenReturn(Optional.of(problem));
        when(reviewSessionRepository.findByProblemIdOrderByReviewedAtAscIdAsc(1))
                .thenReturn(List.of(first, second));

        List<ReviewSession> history = problemService.getReviewHistory(1);

        assertEquals(List.of(first, second), history);
        assertTrue(history.get(0).getReviewedAt().isBefore(history.get(1).getReviewedAt()));
        verify(reviewSessionRepository).findByProblemIdOrderByReviewedAtAscIdAsc(1);
    }

    @Test
    void getReviewHistoryThrowsWhenProblemIsMissing() {
        when(problemRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ProblemNotFoundException.class, () -> problemService.getReviewHistory(999));
        verify(reviewSessionRepository, never()).findByProblemIdOrderByReviewedAtAscIdAsc(any());
    }
}
