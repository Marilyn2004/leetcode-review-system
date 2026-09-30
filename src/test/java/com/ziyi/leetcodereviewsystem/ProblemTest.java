package com.ziyi.leetcodereviewsystem;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProblemTest {

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "2, 3",
            "3, 7",
            "4, 14",
            "5, 30",
            "6, 30"
    })
    void intervalDaysFollowSpacedRepetitionSchedule(int reviewNumber, long expectedDays) {
        assertEquals(expectedDays, Problem.intervalDaysForReview(reviewNumber));
    }

    @Test
    void markReviewedIncrementsCountAndSchedulesNextDate() {
        Problem problem = new Problem(
                1,
                "Two Sum",
                "Easy",
                "HashMap",
                "notes",
                0,
                true,
                null,
                null
        );

        problem.markReviewed();

        LocalDate today = LocalDate.now();
        assertEquals(1, problem.getTimesReviewed());
        assertEquals(today, problem.getLastReviewed());
        assertEquals(today.plusDays(1), problem.getNextReviewDate());
    }

    @Test
    void markReviewedUsesThirtyDayIntervalFromFifthReviewOnward() {
        Problem problem = new Problem(
                1,
                "Two Sum",
                "Easy",
                "HashMap",
                "notes",
                4,
                true,
                LocalDate.now().minusDays(14),
                LocalDate.now()
        );

        problem.markReviewed();

        LocalDate today = LocalDate.now();
        assertEquals(5, problem.getTimesReviewed());
        assertEquals(today, problem.getLastReviewed());
        assertEquals(today.plusDays(30), problem.getNextReviewDate());
    }
}
