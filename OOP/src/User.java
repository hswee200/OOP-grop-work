import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private Budget budget;
    private ArrayList<EmissionTransaction> transactions;

    public User(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
        this.transactions = new ArrayList<>();
    }

    public void setBudget(double amount, Period period) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Budget amount must be greater than zero.");
        }
        if (period == null) {
            throw new IllegalArgumentException("Period cannot be null.");
        }
        this.budget = new Budget(amount, period);
    }

    public String getName() {
        return name;
    }

    public Budget getBudget() {
        return budget;
    }

    // ------------------------------------------------------
    // LOG ACTIVITY
    // ------------------------------------------------------
    public void logActivity(Category category, double quantity) {
        if (budget == null) {
            throw new IllegalStateException("Set a budget before logging activities.");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive number.");
        }

        double emission = EmissionCalculator.calculateEmission(category, quantity);

        // Check if budget allows this activity
        if (!budget.canAfford(emission)) {
            System.out.println("\n⚠️  Cannot log this activity!");
            System.out.println("Required emission: " + emission + " kg CO₂");
            System.out.println("Remaining budget: " + budget.getRemainingBudget() + " kg CO₂");
            System.out.println("You cannot exceed your carbon budget.");
            System.out.println("Try entering a smaller quantity.\n");
            return;
        }

        // Deduct from budget
        budget.deductEmission(emission);

        // Create and store transaction
        EmissionTransaction tx = new EmissionTransaction(category, quantity, emission);
        transactions.add(tx);

        System.out.println("Activity logged: " + tx);

        // Optional: auto-save after each log
        saveHistoryToFile();
    }

    // ------------------------------------------------------
    // DISPLAY SUMMARY
    // ------------------------------------------------------
    public void displaySummary() {
        if (budget == null) {
            System.out.println("You must set a budget first.");
            return;
        }

        System.out.println("\n===== EMISSION SUMMARY FOR " + name + " =====");
        System.out.println("Period: " + budget.getPeriod().getDisplayName());
        System.out.println("Initial Budget: " + budget.getInitialBudget() + " kg");
        System.out.println("Used: " + budget.getTotalBudgetUsed() + " kg");
        System.out.println("Remaining: " + budget.getRemainingBudget() + " kg");
        System.out.println("Percentage Used: " + budget.getPercentageBudgetUsed() + "%");
        System.out.println("Over Budget? " + (budget.isOverBudget() ? "YES" : "NO"));
        System.out.println("--------------------------------------------");
        System.out.println("Logged Activities:");

        if (transactions.isEmpty()) {
            System.out.println("No activities logged yet.");
        } else {
            for (EmissionTransaction tx : transactions) {
                System.out.println(tx);
            }
        }

        // Save summary to file each time
        saveHistoryToFile();
    }

    // ------------------------------------------------------
    // FILE I/O: SAVE HISTORY
    // ------------------------------------------------------
    public void saveHistoryToFile() {
        String fileName = name + "_carbon_history.txt";

        try (FileWriter writer = new FileWriter(fileName, false)) {

            writer.write("===== CARBON EMISSION HISTORY FOR " + name + " =====\n");
            writer.write("Period: " + budget.getPeriod().getDisplayName() + "\n");
            writer.write("Initial Budget: " + budget.getInitialBudget() + " kg\n");
            writer.write("Used: " + budget.getTotalBudgetUsed() + " kg\n");
            writer.write("Remaining: " + budget.getRemainingBudget() + " kg\n");
            writer.write("Over Budget?: " + (budget.isOverBudget() ? "YES" : "NO") + "\n");
            writer.write("-------------------------------------------------------\n");
            writer.write("DATE & TIME | CATEGORY | QUANTITY | EMISSION (kg CO2e)\n");
            writer.write("-------------------------------------------------------\n");

            if (transactions.isEmpty()) {
                writer.write("No activities logged.\n");
            } else {
                for (EmissionTransaction tx : transactions) {
                    writer.write(tx.toString() + "\n");
                }
            }

            writer.write("\nEnd of Report.\n");

            System.out.println("History saved to file: " + fileName);

        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
    public List<EmissionTransaction> getTransactions() {
        return new ArrayList<>(transactions);
    }


}

