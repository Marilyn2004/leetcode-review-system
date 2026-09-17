public class Problem {
    private int id;
    private String title;
    private String difficulty;
    private String pattern;
    private String notes;
    private int timesReviewed;
    private boolean solved;

    public Problem(int id, String title, String difficulty, String pattern, String notes, boolean solved) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.pattern = pattern;
        this.notes = notes;
        this.timesReviewed = 0;
        this.solved = solved;
    }

    public Problem(int id, String title, String difficulty, String pattern, String notes, int timesReviewed, boolean solved) {

        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.pattern = pattern;
        this.notes = notes;
        this.timesReviewed = timesReviewed;
        this.solved = solved;
    }

    public int getId() {
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

    public void markReviewed() {
        timesReviewed++;
    }


    public String toFileString() {
        return id + "," + title + "," + difficulty + "," + pattern + "," + notes + "," + timesReviewed + "," + solved;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + title +
                " | " + difficulty +
                " | Pattern: " + pattern +
                " | Reviewed: " + timesReviewed +
                " | Solved: " + solved +
                "\nNotes: " + notes;
    }
}
