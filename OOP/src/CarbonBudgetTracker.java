import java.util.Scanner;

public class CarbonBudgetTracker {

    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║   PERSONAL CARBON BUDGET TRACKER               ║");
        System.out.println("║   Track Your Carbon Footprint & Save the Earth ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");

        setupUser();
        mainMenu();
    }

    // ------------------------------------------------------------
    // USER SETUP
    // ------------------------------------------------------------
    private static void setupUser() {
        System.out.println("Welcome to the Carbon Budget Tracker!");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();
        currentUser = new User(name);

        System.out.print("Enter your budget amount: ");
        double amount = getDoubleInput();

        System.out.println("Select a period (WEEK or MONTH): ");
        Period period = getPeriodInput();

        currentUser.setBudget(amount, period);
        System.out.println("Budget set successfully!\n");
    }

    // ------------------------------------------------------------
    // MAIN MENU
    // ------------------------------------------------------------
    private static void mainMenu() {
        while (true) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║                 MAIN MENU                  ║");
            System.out.println("╚════════════════════════════════════════════╝\n");

            System.out.println("1. Log activity");
            System.out.println("2. View summary");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();

            switch (choice) {
                case 1 -> logActivity();
                case 2 -> currentUser.displaySummary();
                case 3 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    // ------------------------------------------------------------
    // LOG ACTIVITY
    // ------------------------------------------------------------
    private static void logActivity() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           LOG EMISSION ACTIVITY            ║");
        System.out.println("╚════════════════════════════════════════════╝\n");

        // Display categories
        System.out.println("Select a category:");
        for (Category c : Category.values()) {
            System.out.println("- " + c);
        }

        System.out.print("Enter category: ");
        Category category = getCategoryInput();

        System.out.print("Enter quantity (" + category.getUnit() + "): ");
        double qty = getDoubleInput();

        try {
            currentUser.logActivity(category, qty);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------
    // CATEGORY INPUT (SAFE)
    // ------------------------------------------------------------
    private static Category getCategoryInput() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();

            try {
                return Category.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid category. Try again.");
                System.out.print("Enter category: ");
            }
        }
    }

    // ------------------------------------------------------------
    // PERIOD INPUT (SAFE)
    // ------------------------------------------------------------
    private static Period getPeriodInput() {
        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();

            try {
                return Period.valueOf(input);
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid period. Enter WEEK or MONTH: ");
            }
        }
    }

    // ------------------------------------------------------------
    // INTEGER INPUT (SAFE)
    // ------------------------------------------------------------
    private static int getIntInput() {
        while (true) {
            String line = scanner.nextLine().trim();

            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid integer: ");
            }
        }
    }

    // ------------------------------------------------------------
    // DOUBLE INPUT (SAFE)
    // ------------------------------------------------------------
    private static double getDoubleInput() {
        while (true) {
            String line = scanner.nextLine().trim();

            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid decimal: ");
            }
        }
    }
}
