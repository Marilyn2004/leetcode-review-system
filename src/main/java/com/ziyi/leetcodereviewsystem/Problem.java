package com.ziyi.leetcodereviewsystem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

import java.time.LocalDate;

@Entity
@Table(name = "problems", uniqueConstraints = {
        @UniqueConstraint(name = "uk_problem_platform_external", columnNames = {"platform", "external_problem_id"}),
        @UniqueConstraint(name = "uk_problem_platform_slug", columnNames = {"platform", "slug"})
})
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "title must not be blank")
    private String title;
    @NotBlank(message = "difficulty must not be blank")
    @jakarta.validation.constraints.Pattern(regexp = "Easy|Medium|Hard", message = "difficulty must be Easy, Medium, or Hard")
    private String difficulty;
    @NotBlank(message = "pattern must not be blank")
    private String pattern;
    private String notes;
    @Min(value = 0, message = "timesReviewed must not be negative")
    private int timesReviewed;
    private boolean solved;
    private LocalDate lastReviewed;
    private LocalDate nextReviewDate;

    // Catalog metadata is hidden from the legacy V1 entity JSON contract.
    @JsonIgnore
    private String platform;
    @JsonIgnore
    @Column(name = "external_problem_id")
    private String externalProblemId;
    @JsonIgnore
    private String slug;
    @JsonIgnore
    private String url;
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "problem_patterns",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "pattern_id"),
            uniqueConstraints = @UniqueConstraint(name = "uk_problem_pattern", columnNames = {"problem_id", "pattern_id"}))
    private Set<Pattern> patterns = new LinkedHashSet<>();

    @JsonIgnore
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    @JsonIgnore
    public String getExternalProblemId() { return externalProblemId; }
    public void setExternalProblemId(String externalProblemId) { this.externalProblemId = externalProblemId; }
    @JsonIgnore
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    @JsonIgnore
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    @JsonIgnore
    public Set<Pattern> getPatterns() { return patterns; }

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
