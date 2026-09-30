package com.ziyi.leetcodereviewsystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProblemServiceTest {

    @Mock
    private ProblemRepository problemRepository;

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
    void updateProblemReturnsNullWhenIdIsMissing() {
        when(problemRepository.findById(999)).thenReturn(Optional.empty());
        assertNull(problemService.updateProblem(999, new Problem()));
        verify(problemRepository, never()).save(any());
    }

    @Test
    void deleteProblemByIdRemovesExistingProblem() {
        when(problemRepository.existsById(1)).thenReturn(true);

        assertTrue(problemService.deleteProblemById(1));
        verify(problemRepository).deleteById(1);
    }

    @Test
    void deleteProblemByIdReturnsFalseWhenIdIsMissing() {
        when(problemRepository.existsById(999)).thenReturn(false);

        assertFalse(problemService.deleteProblemById(999));
        verify(problemRepository, never()).deleteById(any());
    }
}
