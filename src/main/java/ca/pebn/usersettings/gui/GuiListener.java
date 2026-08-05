package ca.pebn.usersettings.gui;

import ca.pebn.usersettings.data.BackpackKeepMode;
import ca.pebn.usersettings.data.ExpLossType;
import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class GuiListener implements Listener {

    private final PlayerSettingsManager settingsManager;

    public GuiListener(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof PlayerSelectorGui selectorGui) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player clicker)) return;

            int rawSlot = event.getRawSlot();
            if (rawSlot >= 0 && rawSlot < 45) {
                ItemStack item = event.getCurrentItem();
                if (item != null && item.getItemMeta() instanceof SkullMeta meta) {
                    OfflinePlayer target = meta.getOwningPlayer();
                    if (target != null) {
                        UserSettingsData settings = settingsManager.getSettings(target);
                        clicker.openInventory(new UserSettingsEditorGui(target, settings).getInventory());
                    }
                }
            } else if (rawSlot == 48) { // Prev page
                selectorGui.setPage(selectorGui.getPage() - 1);
            } else if (rawSlot == 50) { // Next page
                selectorGui.setPage(selectorGui.getPage() + 1);
            }

        } else if (holder instanceof UserSettingsEditorGui editorGui) {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player clicker)) return;

            int rawSlot = event.getRawSlot();
            UserSettingsData settings = editorGui.getSettings();

            switch (rawSlot) {
                case 10 -> { // Hotbar toggle
                    settings.setKeepHotbar(!settings.isKeepHotbar());
                    settingsManager.saveSettings(settings);
                    editorGui.refreshInventory();
                    clicker.sendMessage(Component.text("Hotbar retention set to: ", NamedTextColor.GRAY)
                            .append(settings.isKeepHotbar() ? Component.text("ENABLED", NamedTextColor.GREEN) : Component.text("DISABLED", NamedTextColor.RED)));
                }
                case 12 -> { // Backpack Mode
                    settings.setBackpackKeepMode(settings.getBackpackKeepMode().next());
                    settingsManager.saveSettings(settings);
                    editorGui.refreshInventory();
                    clicker.sendMessage(Component.text("Backpack retention mode set to: ", NamedTextColor.GRAY)
                            .append(Component.text(settings.getBackpackKeepMode().getDisplayName(), NamedTextColor.AQUA)));
                }
                case 13 -> { // Backpack Count
                    int count = settings.getBackpackKeepCount();
                    if (event.isLeftClick()) {
                        count = Math.min(27, count + 1);
                    } else if (event.isRightClick()) {
                        count = Math.max(0, count - 1);
                    }
                    settings.setBackpackKeepCount(count);
                    if (settings.getBackpackKeepMode() == BackpackKeepMode.KEEP_NONE && count > 0) {
                        settings.setBackpackKeepMode(BackpackKeepMode.KEEP_COUNT);
                    }
                    settingsManager.saveSettings(settings);
                    editorGui.refreshInventory();
                    clicker.sendMessage(Component.text("Backpack keep count set to: ", NamedTextColor.GRAY)
                            .append(Component.text(count, NamedTextColor.GREEN)));
                }
                case 15 -> { // EXP Loss Type
                    settings.setExpLossType(settings.getExpLossType().next());
                    settingsManager.saveSettings(settings);
                    editorGui.refreshInventory();
                    clicker.sendMessage(Component.text("EXP loss type set to: ", NamedTextColor.GRAY)
                            .append(Component.text(settings.getExpLossType().getDisplayName(), NamedTextColor.LIGHT_PURPLE)));
                }
                case 16 -> { // EXP Loss Percentage
                    double pct = settings.getExpLossPercentage();
                    if (event.isLeftClick()) {
                        pct = Math.min(100.0, pct + 10.0);
                    } else if (event.isRightClick()) {
                        pct = Math.max(0.0, pct - 10.0);
                    }
                    settings.setExpLossPercentage(pct);
                    settingsManager.saveSettings(settings);
                    editorGui.refreshInventory();
                    clicker.sendMessage(Component.text("EXP loss percentage set to: ", NamedTextColor.GRAY)
                            .append(Component.text(String.format("%.0f%%", pct), NamedTextColor.RED)));
                }
                case 22 -> { // Back button
                    clicker.openInventory(new PlayerSelectorGui(settingsManager).getInventory());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof PlayerSelectorGui || holder instanceof UserSettingsEditorGui) {
            event.setCancelled(true);
        }
    }
}
