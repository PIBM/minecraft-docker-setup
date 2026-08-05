package ca.pebn.usersettings.data;

public enum BackpackKeepMode {
    KEEP_NONE("Keep None"),
    KEEP_COUNT("Keep Specific Count"),
    KEEP_ALL("Keep All");

    private final String displayName;

    BackpackKeepMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BackpackKeepMode next() {
        BackpackKeepMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
