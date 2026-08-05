package ca.pebn.usersettings.data;

import java.util.UUID;

public class UserSettingsData {

    private final UUID playerUuid;
    private String lastKnownName;
    private boolean keepHotbar;
    private BackpackKeepMode backpackKeepMode;
    private int backpackKeepCount;
    private ExpLossType expLossType;
    private double expLossPercentage;

    public UserSettingsData(UUID playerUuid, String lastKnownName) {
        this.playerUuid = playerUuid;
        this.lastKnownName = lastKnownName != null ? lastKnownName : "Unknown";
        this.keepHotbar = false;
        this.backpackKeepMode = BackpackKeepMode.KEEP_NONE;
        this.backpackKeepCount = 0;
        this.expLossType = ExpLossType.ALL_LEVELS;
        this.expLossPercentage = 100.0;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        if (lastKnownName != null && !lastKnownName.isEmpty()) {
            this.lastKnownName = lastKnownName;
        }
    }

    public boolean isKeepHotbar() {
        return keepHotbar;
    }

    public void setKeepHotbar(boolean keepHotbar) {
        this.keepHotbar = keepHotbar;
    }

    public BackpackKeepMode getBackpackKeepMode() {
        return backpackKeepMode;
    }

    public void setBackpackKeepMode(BackpackKeepMode backpackKeepMode) {
        this.backpackKeepMode = backpackKeepMode != null ? backpackKeepMode : BackpackKeepMode.KEEP_NONE;
    }

    public int getBackpackKeepCount() {
        return backpackKeepCount;
    }

    public void setBackpackKeepCount(int backpackKeepCount) {
        this.backpackKeepCount = Math.max(0, Math.min(27, backpackKeepCount));
    }

    public ExpLossType getExpLossType() {
        return expLossType;
    }

    public void setExpLossType(ExpLossType expLossType) {
        this.expLossType = expLossType != null ? expLossType : ExpLossType.ALL_LEVELS;
    }

    public double getExpLossPercentage() {
        return expLossPercentage;
    }

    public void setExpLossPercentage(double expLossPercentage) {
        this.expLossPercentage = Math.max(0.0, Math.min(100.0, expLossPercentage));
    }
}
