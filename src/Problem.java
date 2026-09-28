import java.time.LocalDate;

public class Problem {
    private int id;
    private String title;
    private String difficulty;
    private String pattern;
    private String notes;
    private int timesReviewed;
    private boolean solved;
    private LocalDate lastReviewed;
    private LocalDate nextReviewDate;

    public Problem(int id, String title, String difficulty,
                   String pattern, String notes, boolean solved) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.pattern = pattern;
        this.notes = notes;
        this.timesReviewed = 0;
        this.solved = solved;
        this.lastReviewed = null;
        this.nextReviewDate = LocalDate.now();
    }

    public Problem(int id, String title, String difficulty,
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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getTimesReviewed() {
        return timesReviewed;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public void markReviewed() {
        timesReviewed++;
        lastReviewed = LocalDate.now();
        int interval;
        if (timesReviewed == 1) {
            interval = 1;
        }
        else if (timesReviewed == 2) {
            interval = 3;
        }
        else if (timesReviewed == 3) {
            interval = 7;
        }
        else if (timesReviewed == 4) {
            interval = 14;
        }
        else{
            interval = 30;
        }
        nextReviewDate = lastReviewed.plusDays(interval);

    }


    public String toFileString() {
        return id + "," + title + "," + difficulty + "," + pattern + "," + notes + "," + timesReviewed + "," + solved + "," +
                (lastReviewed == null ? "" : lastReviewed.toString()) + "," + nextReviewDate.toString();
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title +
                " | " + difficulty +
                " | Pattern: " + pattern +
                " | Reviewed: " + timesReviewed +
                " | Solved: " + solved +  "\nLast Reviewed: " +
                (lastReviewed == null ? "Never" : lastReviewed.toString()) +
                "\nNext Review: " + nextReviewDate +
                "\nNotes: " + notes ;
    }
}
