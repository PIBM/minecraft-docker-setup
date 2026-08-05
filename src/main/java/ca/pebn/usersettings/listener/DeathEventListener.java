package ca.pebn.usersettings.listener;

import ca.pebn.usersettings.data.BackpackKeepMode;
import ca.pebn.usersettings.data.ExpLossType;
import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class DeathEventListener implements Listener {

    private final PlayerSettingsManager settingsManager;

    public DeathEventListener(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UserSettingsData settings = settingsManager.getSettings(player);

        handleInventoryRetention(event, player, settings);
        handleExperienceLoss(event, player, settings);
    }

    private void handleInventoryRetention(PlayerDeathEvent event, Player player, UserSettingsData settings) {
        PlayerInventory inv = player.getInventory();
        List<ItemStack> drops = event.getDrops();
        List<ItemStack> itemsToKeep = event.getItemsToKeep();

        // 1. Hotbar Retention (Slots 0 to 8)
        if (settings.isKeepHotbar()) {
            for (int slot = 0; slot <= 8; slot++) {
                ItemStack item = inv.getItem(slot);
                if (item != null && !item.getType().isAir()) {
                    if (removeFromList(drops, item)) {
                        itemsToKeep.add(item);
                    }
                }
            }
        }

        // 2. Backpack Retention (Main Inventory Slots 9 to 35)
        BackpackKeepMode mode = settings.getBackpackKeepMode();
        if (mode == BackpackKeepMode.KEEP_ALL) {
            for (int slot = 9; slot <= 35; slot++) {
                ItemStack item = inv.getItem(slot);
                if (item != null && !item.getType().isAir()) {
                    if (removeFromList(drops, item)) {
                        itemsToKeep.add(item);
                    }
                }
            }
        } else if (mode == BackpackKeepMode.KEEP_COUNT) {
            int keepLimit = settings.getBackpackKeepCount();
            int keptSoFar = 0;

            for (int slot = 9; slot <= 35 && keptSoFar < keepLimit; slot++) {
                ItemStack item = inv.getItem(slot);
                if (item != null && !item.getType().isAir()) {
                    if (removeFromList(drops, item)) {
                        itemsToKeep.add(item);
                        keptSoFar++;
                    }
                }
            }
        }
    }

    private void handleExperienceLoss(PlayerDeathEvent event, Player player, UserSettingsData settings) {
        double lossPercent = settings.getExpLossPercentage() / 100.0;
        double keepPercent = 1.0 - lossPercent;

        if (settings.getExpLossType() == ExpLossType.CURRENT_LEVEL) {
            // Keep player level, reduce current level progress
            int currentLevel = player.getLevel();
            int expToNext = getExpToNext(currentLevel);
            int currentExpInLevel = Math.round(expToNext * player.getExp());
            int newExpInLevel = (int) Math.round(currentExpInLevel * keepPercent);

            event.setNewLevel(currentLevel);
            event.setNewExp(newExpInLevel);
        } else {
            // ALL_LEVELS: Calculate total experience
            int totalExp = getTotalExperience(player);
            int newTotalExp = (int) Math.round(totalExp * keepPercent);

            int newLevel = getLevelFromExp(newTotalExp);
            int expForNewLevel = getExpAtLevel(newLevel);
            int expInCurrentLevel = newTotalExp - expForNewLevel;

            event.setNewLevel(newLevel);
            event.setNewExp(expInCurrentLevel);
            event.setNewTotalExp(newTotalExp);
        }
    }

    private boolean removeFromList(List<ItemStack> list, ItemStack target) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack != null && stack.isSimilar(target)) {
                if (stack.getAmount() == target.getAmount()) {
                    list.remove(i);
                    return true;
                } else if (stack.getAmount() > target.getAmount()) {
                    stack.setAmount(stack.getAmount() - target.getAmount());
                    return true;
                }
            }
        }
        return false;
    }

    // Minecraft XP formula helpers
    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        int expAtLevel = getExpAtLevel(level);
        int expInLevel = Math.round(getExpToNext(level) * player.getExp());
        return expAtLevel + expInLevel;
    }

    private int getExpAtLevel(int level) {
        if (level <= 16) {
            return level * level + 6 * level;
        } else if (level <= 31) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        }
    }

    private int getExpToNext(int level) {
        if (level <= 15) {
            return 2 * level + 7;
        } else if (level <= 30) {
            return 5 * level - 38;
        } else {
            return 9 * level - 158;
        }
    }

    private int getLevelFromExp(int exp) {
        if (exp <= 0) return 0;
        int level = 0;
        while (getExpAtLevel(level + 1) <= exp) {
            level++;
        }
        return level;
    }
}
