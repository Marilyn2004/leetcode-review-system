package com.ziyi.leetcodereviewsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Entity
@Table(name = "problems")
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "title must not be blank")
    private String title;
    @NotBlank(message = "difficulty must not be blank")
    @Pattern(regexp = "Easy|Medium|Hard", message = "difficulty must be Easy, Medium, or Hard")
    private String difficulty;
    @NotBlank(message = "pattern must not be blank")
    private String pattern;
    private String notes;
    @Min(value = 0, message = "timesReviewed must not be negative")
    private int timesReviewed;
    private boolean solved;
    private LocalDate lastReviewed;
    private LocalDate nextReviewDate;

    public Problem(Integer id, String title, String difficulty,
                   String pattern, String notes, int timesReviewed, boolean solved, LocalDate lastReviewed,
                   LocalDate nextReviewDate) {

        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.pattern = pattern;
        this.notes = notes;
        this.timesReviewed = timesReviewed;
        this.solved = solved;
        this.lastReviewed = lastReviewed;
        this.nextReviewDate = nextReviewDate;
    }

    public Problem() {

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setTimesReviewed(int timesReviewed) {
        this.timesReviewed = timesReviewed;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void setLastReviewed(LocalDate lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public void setNextReviewDate(LocalDate nextReviewDate) {
        this.nextReviewDate = nextReviewDate;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getPattern() {
        return pattern;
    }

    public String getNotes() {
        return notes;
    }

    public int getTimesReviewed() {
        return timesReviewed;
    }

    public boolean isSolved() {
        return solved;
    }

    public LocalDate getLastReviewed() {
        return lastReviewed;
    }

    public LocalDate getNextReviewDate() {
        return nextReviewDate;
    }

    public void markReviewed() {
        timesReviewed++;
        lastReviewed = LocalDate.now();
        nextReviewDate = lastReviewed.plusDays(intervalDaysForReview(timesReviewed));
    }

    static long intervalDaysForReview(int reviewNumber) {
        return switch (reviewNumber) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 7;
            case 4 -> 14;
            default -> 30;
        };
    }
}
