import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StorageManager storageManager = new StorageManager("problems.csv");
        ArrayList<Problem> savedProblems = storageManager.loadProblems();
        ReviewManager manager = new ReviewManager(savedProblems);
        int choice = 0;
        while (choice != 9) {
            System.out.println("=== LeetCode Review System ===");
            System.out.println("1. Add Problem");
            System.out.println("2. List All Problems");
            System.out.println("3. Search by Difficulty");
            System.out.println("4. Search by Pattern");
            System.out.println("5. Review a Problem");
            System.out.println("6. Edit a Problem");
            System.out.println("7. Delete a Problem");
            System.out.println("8. Show Problems Should be Reviewed Today");
            System.out.println("9. Exit");
            System.out.print("Select your choice: ");
            choice = readInt(scanner);
            if (choice < 1 || choice > 9) {
                System.out.println("Please enter a number between 1 and 9.");
                continue;
            }
            if (choice == 1) {
                Problem problem = createProblemFromUserInput(scanner, manager);
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
                System.out.print("Enter problem ID: ");
                int id = readInt(scanner);
                Problem problem = manager.findProblemById(id);
                if (problem == null) {
                    System.out.println("Problem not found.");
                }
                else{
                    problem.markReviewed();
                    System.out.println("Problem reviewed successfully!");
                }

            }
            else if (choice == 6){
                System.out.print("Enter problem ID: ");
                int id = readInt(scanner);
                Problem problem = manager.findProblemById(id);
                if (problem == null) {
                    System.out.println("Problem not found.");
                }
                else {
                    editProblemFromUserInput(scanner, problem);
                    System.out.println("Problem edited successfully!");
                }

            }
            else if (choice == 7) {
                System.out.print("Enter problem ID: ");
                int id = readInt(scanner);
                Problem problem = manager.findProblemById(id);
                if (problem == null) {
                    System.out.println("Problem not found.");
                }
                else{
                    manager.deleteProblem(problem);
                    System.out.println("Problem deleted successfully!");
                }
            }
            else if (choice == 8){
                manager.listDueProblems();
            }

            else if (choice == 9){
                storageManager.saveProblems(manager.getProblems());
                System.out.println("Goodbye!");
            }
            else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

    }

    private static Problem createProblemFromUserInput(Scanner scanner, ReviewManager manager) {
        int id = manager.generateNextId();

        System.out.print("Enter title: ");
        String title = scanner.nextLine();

        System.out.print("Enter difficulty: ");
        String difficulty = readDifficulty(scanner);

        System.out.print("Enter pattern: ");
        String pattern = scanner.nextLine();

        System.out.print("Enter notes: ");
        String notes = scanner.nextLine();

        System.out.print("Solved? true/false: ");
        boolean solved = readBoolean(scanner);

        return new Problem(id, title, difficulty, pattern, notes, solved);
    }

    private static void editProblemFromUserInput(Scanner scanner, Problem problem) {
        System.out.print("Enter new title: ");
        String newTitle = scanner.nextLine();
        problem.setTitle(newTitle);

        System.out.print("Enter new difficulty: ");
        String newDifficulty = readDifficulty(scanner);
        problem.setDifficulty((newDifficulty));

        System.out.print("Enter new pattern: ");
        String newPattern = scanner.nextLine();
        problem.setPattern((newPattern));

        System.out.print("Enter new notes: ");
        String newNotes = scanner.nextLine();
        problem.setNotes((newNotes));

        System.out.print("Enter new solved condition: ");
        boolean newSolved = readBoolean(scanner);
        problem.setSolved((newSolved));
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    private static boolean readBoolean(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("true")) {
                return true;
            } else if (input.equalsIgnoreCase("false")) {
                return false;
            }
            System.out.println("Invalid input. Please enter true or false.");
        }
    }

    private static String readDifficulty(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("Easy")) {
                return "Easy";
            } else if (input.equalsIgnoreCase("Medium")) {
                return "Medium";
            } else if (input.equalsIgnoreCase("Hard")) {
                return "Hard";
            }
            System.out.println("Invalid difficulty. Please enter Easy, Medium, or Hard.");
        }
    }
}


