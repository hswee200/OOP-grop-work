public enum Category {
    CAR(0.21, "km"),
    BUS(0.12, "km"),
    TRAIN(0.10, "km"),
    FLIGHT_SHORT(0.30, "hour"),
    FLIGHT_LONG(0.40, "hour"),
    ELECTRICITY(0.40, "kWh"),
    GAS(2.0, "kg"),
    MEAL_MEAT(2.5, "meal"),
    MEAL_VEGETARIAN(1.5, "meal"),
    MEAL_VEGAN(1.0, "meal"),
    WASTE(1.2, "kg");

    private final double emissionFactor;
    private final String unit;

    Category(double emissionFactor,String unit) {
        this.emissionFactor = emissionFactor;
        this.unit=unit;
    }


    public double getEmissionFactor() {
        return emissionFactor;
    }

    public String getUnit() {
        return unit;
    }
    @Override
    public String toString() {
        return name() + " (" + emissionFactor + " kg CO₂ per " + unit + ")";
    }


}