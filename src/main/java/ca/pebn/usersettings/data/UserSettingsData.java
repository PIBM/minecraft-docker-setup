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
    private boolean keepArmor;
    private double armorDamagePercentage;

    // Static global defaults (Updated from YAML on startup)
    public static boolean DEFAULT_KEEP_HOTBAR = false;
    public static BackpackKeepMode DEFAULT_BACKPACK_MODE = BackpackKeepMode.KEEP_NONE;
    public static int DEFAULT_BACKPACK_COUNT = 0;
    public static ExpLossType DEFAULT_EXP_LOSS_TYPE = ExpLossType.ALL_LEVELS;
    public static double DEFAULT_EXP_PERCENTAGE = 100.0;
    public static boolean DEFAULT_KEEP_ARMOR = false;
    public static double DEFAULT_ARMOR_DAMAGE_PERCENTAGE = 0.0;

    public UserSettingsData(UUID playerUuid, String lastKnownName) {
        this.playerUuid = playerUuid;
        this.lastKnownName = lastKnownName != null ? lastKnownName : "Unknown";

        // Load defaults from static fields instead of hardcoding
        this.keepHotbar = DEFAULT_KEEP_HOTBAR;
        this.backpackKeepMode = DEFAULT_BACKPACK_MODE;
        this.backpackKeepCount = DEFAULT_BACKPACK_COUNT;
        this.expLossType = DEFAULT_EXP_LOSS_TYPE;
        this.expLossPercentage = DEFAULT_EXP_PERCENTAGE;
        this.keepArmor = DEFAULT_KEEP_ARMOR;
        this.armorDamagePercentage = DEFAULT_ARMOR_DAMAGE_PERCENTAGE;
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

    public boolean isKeepArmor() {
        return keepArmor;
    }

    public void setKeepArmor(boolean keepArmor) {
        this.keepArmor = keepArmor;
    }

    public double getArmorDamagePercentage() {
        return armorDamagePercentage;
    }

    public void setArmorDamagePercentage(double percentage) {
        // Clamp between 0% and 25%, step of +/-1% is enforced at UI level if needed
        this.armorDamagePercentage = Math.max(0.0, Math.min(25.0, percentage));
    }
}
