import java.util.ArrayList;

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
}
