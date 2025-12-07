public class EmissionCalculator {
    public static double calculateEmission(Category category, double quantity) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be a positive value.");
        }

        return category.getEmissionFactor() * quantity;
    }
}
