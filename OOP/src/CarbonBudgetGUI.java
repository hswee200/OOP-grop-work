import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * CarbonBudgetGUI
 * Swing GUI frontend that works with your existing User / Budget / Transaction classes.
 *
 * Assumes existing classes:
 * - User (with methods: setBudget(double, Period), logActivity(Category,double), displaySummary(), saveHistoryToFile(), getName(), getBudget(), getTransactions())
 * - Category (enum with getUnit() and toString())
 * - Period (enum)
 * - EmissionTransaction (toString() returns readable record or has getters)
 *
 * Replace package line if needed.
 */
public class CarbonBudgetGUI {

    private JFrame frame;
    private JTextField nameField;
    private JTextField budgetField;
    private JComboBox<Period> periodCombo;
    private JButton setupButton;

    private JTable txTable;
    private DefaultTableModel tableModel;
    private JLabel remainingLabel;
    private JLabel usedLabel;
    private JLabel initialLabel;
    private User currentUser;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a");

    public static void main(String[] args) {
        // Launch GUI on EDT
        SwingUtilities.invokeLater(() -> {
            CarbonBudgetGUI app = new CarbonBudgetGUI();
            app.showSetupDialog();
        });
    }

    private void showSetupDialog() {
        frame = new JFrame("Personal Carbon Budget Tracker - Setup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 260);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        // Budget
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Budget (kg CO₂):"), gbc);
        budgetField = new JTextField(10);
        gbc.gridx = 1;
        form.add(budgetField, gbc);

        // Period
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Period:"), gbc);
        periodCombo = new JComboBox<>(Period.values());
        gbc.gridx = 1;
        form.add(periodCombo, gbc);

        // Setup button
        setupButton = new JButton("Create Profile & Open Tracker");
        setupButton.addActionListener(e -> onSetup());
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        form.add(setupButton, gbc);

        frame.add(form, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void onSetup() {
        String name = nameField.getText().trim();
        String budgetText = budgetField.getText().trim();
        Period period = (Period) periodCombo.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter a name.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(budgetText);
            if (budget <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Enter a valid positive budget.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // create user
        try {
            currentUser = new User(name);
            currentUser.setBudget(budget, period);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(frame, "Error creating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        frame.dispose();
        buildMainFrame();
    }

    private void buildMainFrame() {
        JFrame main = new JFrame("Carbon Budget Tracker - " + currentUser.getName());
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setSize(800, 520);
        main.setLocationRelativeTo(null);
        main.setLayout(new BorderLayout(10, 10));

        // Top panel: budget summary
        JPanel top = new JPanel(new GridLayout(1, 4, 8, 8));
        initialLabel = new JLabel("Initial: " + fmt(currentUser.getBudget().getInitialBudget()) + " kg");
        usedLabel = new JLabel("Used: " + fmt(currentUser.getBudget().getTotalBudgetUsed()) + " kg");
        remainingLabel = new JLabel("Remaining: " + fmt(currentUser.getBudget().getRemainingBudget()) + " kg");
        JLabel periodLabel = new JLabel("Period: " + currentUser.getBudget().getPeriod().getDisplayName());
        top.add(initialLabel);
        top.add(usedLabel);
        top.add(remainingLabel);
        top.add(periodLabel);

        main.add(top, BorderLayout.NORTH);

        // Center: transaction table
        tableModel = new DefaultTableModel(new String[]{"Timestamp", "Category", "Quantity", "Unit", "Emission (kg CO2e)"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        txTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(txTable);
        main.add(scroll, BorderLayout.CENTER);

        // Right: controls
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JButton logBtn = new JButton("Log Activity");
        JButton summaryBtn = new JButton("View Summary");
        JButton saveBtn = new JButton("Save History");
        JButton exitBtn = new JButton("Exit");

        logBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        summaryBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(Box.createVerticalStrut(20));
        right.add(logBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(summaryBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(saveBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(exitBtn);

        main.add(right, BorderLayout.EAST);

        // attach actions
        logBtn.addActionListener(e -> showLogDialog(main));
        summaryBtn.addActionListener(e -> {
            currentUser.displaySummary();
            refreshSummary();
        });
        saveBtn.addActionListener(e -> currentUser.saveHistoryToFile());
        exitBtn.addActionListener(e -> {
            currentUser.saveHistoryToFile();
            main.dispose();
        });

        // populate table if there are existing transactions
        refreshTransactions();

        main.setVisible(true);
    }

    private void refreshTransactions() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (EmissionTransaction tx : currentUser.getTransactions()) {
                // If EmissionTransaction has getters, use them; otherwise parse toString()
                String timestamp = tx.getTimestamp(); // expects getTimestamp() returning string
                String category = tx.getCategory().name();
                double qty = tx.getQuantity();
                String unit = tx.getCategory().getUnit();
                double emission = tx.getEmission();
                tableModel.addRow(new Object[]{timestamp, category, fmt(qty), unit, fmt(emission)});
            }
            refreshSummary();
        });
    }

    private void refreshSummary() {
        initialLabel.setText("Initial: " + fmt(currentUser.getBudget().getInitialBudget()) + " kg");
        usedLabel.setText("Used: " + fmt(currentUser.getBudget().getTotalBudgetUsed()) + " kg");
        remainingLabel.setText("Remaining: " + fmt(currentUser.getBudget().getRemainingBudget()) + " kg");
    }

    private void showLogDialog(JFrame owner) {
        JDialog dialog = new JDialog(owner, "Log Activity", true);
        dialog.setSize(380, 220);
        dialog.setLocationRelativeTo(owner);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Category:"), gbc);
        JComboBox<Category> catCombo = new JComboBox<>(Category.values());
        gbc.gridx = 1;
        dialog.add(catCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Quantity:"), gbc);
        JTextField qtyField = new JTextField(10);
        gbc.gridx = 1;
        dialog.add(qtyField, gbc);

        JButton submit = new JButton("Log");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(submit, gbc);

        submit.addActionListener(e -> {
            Category cat = (Category) catCombo.getSelectedItem();
            String s = qtyField.getText().trim();
            double qty;
            try {
                qty = Double.parseDouble(s);
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Enter a valid positive number for quantity.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                currentUser.logActivity(cat, qty);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // after logging, update table and labels
            refreshTransactions();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private String fmt(double v) {
        return String.format("%.2f", v);
    }
}
