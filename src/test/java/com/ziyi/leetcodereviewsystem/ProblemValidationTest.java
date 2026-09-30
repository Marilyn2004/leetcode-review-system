package com.ziyi.leetcodereviewsystem;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProblemValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validProblemHasNoViolations() {
        Problem problem = new Problem(null, "Two Sum", "Easy", "HashMap", "notes", 0, true, null, null);
        assertTrue(validator.validate(problem).isEmpty());
    }

    @Test
    void blankTitleIsInvalid() {
        Problem problem = new Problem(null, "  ", "Easy", "HashMap", "notes", 0, true, null, null);
        assertHasViolation(problem, "title");
    }

    @Test
    void blankDifficultyIsInvalid() {
        Problem problem = new Problem(null, "Two Sum", "", "HashMap", "notes", 0, true, null, null);
        assertHasViolation(problem, "difficulty");
    }

    @Test
    void unknownDifficultyIsInvalid() {
        Problem problem = new Problem(null, "Two Sum", "Expert", "HashMap", "notes", 0, true, null, null);
        assertHasViolation(problem, "difficulty");
    }

    @Test
    void blankPatternIsInvalid() {
        Problem problem = new Problem(null, "Two Sum", "Medium", " ", "notes", 0, true, null, null);
        assertHasViolation(problem, "pattern");
    }

    @Test
    void negativeTimesReviewedIsInvalid() {
        Problem problem = new Problem(null, "Two Sum", "Hard", "DP", "notes", -1, true, null, null);
        assertHasViolation(problem, "timesReviewed");
    }

    private static void assertHasViolation(Problem problem, String field) {
        Set<ConstraintViolation<Problem>> violations = validator.validate(problem);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(violation -> field.equals(violation.getPropertyPath().toString())));
    }
}
