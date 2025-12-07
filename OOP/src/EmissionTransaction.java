import java.time.LocalDateTime;

public class EmissionTransaction {

    //Data fields
    private final Category category;
    private final double quantity;
    private final double emission;
    private final String time;

    //Constructor
    public EmissionTransaction(Category category, double quantity, double emission) {
        //Check if category is not empty
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        // Check if quantity is a non-negative
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + quantity);
        }
        // Check if emission is a non-negative
        if (emission < 0) {
            throw new IllegalArgumentException("Emission cannot be negative: " + emission);
        }
        this.category = category;
        this.quantity = quantity;
        this.emission = emission;
        this.time = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"));;
    }

    //Getters
    public Category getCategory() {
        return category;
    }
    public double getQuantity() {
        return quantity;
    }
    public double getEmission() {
        return emission;
    }
    public String getTimestamp() {
        return time;
    }

    public String getUnit() {
        return category.getUnit();
    }

    //Calculates what percentage this emission has of a total emission
    public double getPercentage(double total) {
        if (total == 0) {
            return 0;
        }
        return (emission / total) * 100;
    }

    //Checks if this transaction exceeded a given threshold
    public boolean exceedsThreshold(double threshold) {
        return emission > threshold;
    }

    @Override
    public String toString() {
        return String.format("%s | %s: %.2f %s -> %.2f kg CO2e",
                time,
                category,
                quantity,
                category.getUnit(),
                emission);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Emission transaction details\n");
        sb.append(String.format("Timestamp:  %s\n", time));
        sb.append(String.format("Category:   %s\n", category));
        sb.append(String.format("Quantity:   %.2f %s\n", quantity, category.getUnit()));
        sb.append(String.format("Factor:     %.3f kg CO2e per %s\n",
                category.getEmissionFactor(), category.getUnit()));
        sb.append(String.format("Emission:   %.2f kg CO2e\n", emission));
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmissionTransaction obj  = (EmissionTransaction) o;

        return obj.quantity== quantity && obj.emission == emission &&
                category == obj.category;
    }
    }