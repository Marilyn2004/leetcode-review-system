import java.util.ArrayList;
import java.time.LocalDate;

public class ReviewManager {
    private ArrayList<Problem> problems;

    public ReviewManager() {
        problems = new ArrayList<>();
    }

    public ReviewManager(ArrayList<Problem> savedProblems) {
        problems = savedProblems;
    }

    public void addProblem(Problem problem) {
        problems.add(problem);
    }

    public void deleteProblem(Problem problem) {
        problems.remove(problem);
    }

    public ArrayList<Problem> getProblems() {
        return problems;
    }

    public void listAllProblems() {
        for (Problem problem : problems) {
            System.out.println(problem);
        }
    }

    public void listByDifficulty(String difficulty) {
        boolean found = false;
        for(Problem problem : problems) {
            if(problem.getDifficulty().equalsIgnoreCase(difficulty)) {
                System.out.println(problem);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No problems found with difficulty: " + difficulty);
        }
    }

    public void listByPattern(String pattern){
        boolean found = false;
        for(Problem problem : problems) {
            if(problem.getPattern().equalsIgnoreCase(pattern)) {
                System.out.println(problem);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No problems found with pattern: " + pattern);
        }
    }

    public Problem findProblemById(int id) {
        for (Problem problem : problems){
            if (problem.getId() == id){
                return problem;
            }
        }
        return null;
    }

    public void listDueProblems() {
        LocalDate today = LocalDate.now();
        boolean found = false;
        for (Problem problem : problems) {
            if (problem.getNextReviewDate().isBefore(today) || problem.getNextReviewDate().isEqual(today)) {
                System.out.println(problem);
                found = true;
            }
        }
        if(!found){
            System.out.println("No problems need to be reviewed today!");
        }
    }

    public int generateNextId() {
        int maxId = 0;
        for (Problem problem : problems){
            if(problem.getId() > maxId){
                maxId = problem.getId();
            }
        }
        return maxId+1;
    }
}
