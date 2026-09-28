import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.time.LocalDate;

public class StorageManager {

    private String fileName;

    public StorageManager(String fileName) {
        this.fileName = fileName;
    }

    public void saveProblems(ArrayList<Problem> problems) {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            for (Problem problem : problems) {
                writer.println(problem.toFileString());
            }
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error saving problems.");
        }
    }

    public ArrayList<Problem> loadProblems() {
        ArrayList<Problem> problems = new ArrayList<>();
        try {
            File file = new File(fileName);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",", -1);
                int id = Integer.parseInt(parts[0]);
                String title = parts[1];
                String difficulty = parts[2];
                String pattern = parts[3];
                String notes = parts[4];
                int timesReviewed = Integer.parseInt(parts[5]);
                boolean solved = Boolean.parseBoolean(parts[6]);
                LocalDate lastReviewed;
                LocalDate nextReviewDate;
                if (parts.length >= 9) {
                    lastReviewed = parts[7].isEmpty() ? null : LocalDate.parse(parts[7]);
                    nextReviewDate = LocalDate.parse(parts[8]);
                }
                else {
                    lastReviewed = null;
                    nextReviewDate = LocalDate.now();
                }
                Problem problem = new Problem(id, title, difficulty, pattern, notes, timesReviewed, solved, lastReviewed, nextReviewDate);
                problems.add(problem);
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
                System.out.println("No saved problems found.");
        }
        return problems;
    }

}
