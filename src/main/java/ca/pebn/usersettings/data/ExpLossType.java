package ca.pebn.usersettings.data;

public enum ExpLossType {
    CURRENT_LEVEL("Current Level Only"),
    ALL_LEVELS("All Levels (Total EXP)");

    private final String displayName;

    ExpLossType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ExpLossType next() {
        ExpLossType[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
