class Budget {
    private final double initialBudget;
    private double remainingBudget;
    private final Period period;

    public Budget(double initialBudget, Period period) {
        if (initialBudget <= 0) {
            throw new IllegalArgumentException("Budget must be positive");
        }
        this.initialBudget = initialBudget;
        this.remainingBudget = initialBudget;
        this.period = period;
    }

    public boolean deductEmission(double emission) {
        if (emission < 0) {
            throw new IllegalArgumentException("Emission cannot be negative");
        }
        remainingBudget -= emission;
        if (remainingBudget < 0) {
            System.out.println("The emission has exceeded the budget");
        }
        return remainingBudget >= 0;
    }

    public double getInitialBudget() {
        return initialBudget;
    }

    public double getRemainingBudget() {
        return remainingBudget;
    }

    public double getTotalBudgetUsed() {
        return initialBudget - remainingBudget;
    }

    public Period getPeriod() {
        return period;
    }
    public double getPercentageBudgetUsed() {
        return (getTotalBudgetUsed() / initialBudget) * 100;
    }
    public void resetBudget() {
        remainingBudget = initialBudget;
    }

    public boolean isOverBudget() {
        return remainingBudget<0;
    }
    public boolean canAfford(double emission) {
        return emission <= remainingBudget;
    }

}