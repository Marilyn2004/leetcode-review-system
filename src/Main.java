import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StorageManager storageManager = new StorageManager("problems.csv");
        ArrayList<Problem> savedProblems = storageManager.loadProblems();
        ReviewManager manager = new ReviewManager(savedProblems);
        int choice = 0;
        while (choice != 5) {
            System.out.println("=== LeetCode Review System ===");
            System.out.println("1. Add Problem");
            System.out.println("2. List All Problems");
            System.out.println("3. Search by Difficulty");
            System.out.println("4. Search by Pattern");
            System.out.println("5. Exit");
            System.out.print("Select your choice: ");
            choice = Integer.parseInt(scanner.nextLine());
            if (choice == 1) {
                Problem problem = createProblemFromUserInput(scanner);
                manager.addProblem(problem);
            }
            else if (choice == 2) {
                manager.listAllProblems();
            }
            else if (choice == 3) {
                System.out.print("Enter difficulty: ");
                String difficulty = scanner.nextLine();
                manager.listByDifficulty(difficulty);
            }
            else if (choice == 4){
                System.out.print("Enter Pattern: ");
                String pattern = scanner.nextLine();
                manager.listByPattern(pattern);
            }
            else if (choice == 5){
                storageManager.saveProblems(manager.getProblems());
                System.out.println("Goodbey!");
            }
            else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

    }

    private static Problem createProblemFromUserInput(Scanner scanner) {
        System.out.print("Enter problem id: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        System.out.print("Enter difficulty: ");
        String difficulty = scanner.nextLine();

        System.out.print("Enter pattern: ");
        String pattern = scanner.nextLine();

        System.out.print("Enter notes: ");
        String notes = scanner.nextLine();

        System.out.print("Solved? true/false: ");
        boolean solved = Boolean.parseBoolean(scanner.nextLine());

        return new Problem(id, title, difficulty, pattern, notes, solved);
    }
}
