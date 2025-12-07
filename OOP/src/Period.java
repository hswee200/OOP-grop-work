public enum Period{
    WEEK("Week",7),
    MONTH("Month",30);

    private final String displayName;
    private final int days;
    Period(String displayName,int days){
        this.displayName=displayName;
        this.days=days;
    }
    public String getDisplayName(){
        return displayName;
    }
    public int getDays(){
        return days;
    }
}

