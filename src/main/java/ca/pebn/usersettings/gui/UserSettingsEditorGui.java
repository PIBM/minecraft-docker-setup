package ca.pebn.usersettings.gui;

import ca.pebn.usersettings.data.BackpackKeepMode;
import ca.pebn.usersettings.data.ExpLossType;
import ca.pebn.usersettings.data.UserSettingsData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class UserSettingsEditorGui implements InventoryHolder {

    private final OfflinePlayer targetPlayer;
    private final UserSettingsData settings;
    private final Inventory inventory;

    public UserSettingsEditorGui(OfflinePlayer targetPlayer, UserSettingsData settings) {
        this.targetPlayer = targetPlayer;
        this.settings = settings;

        String name = targetPlayer.getName() != null ? targetPlayer.getName() : "Unknown";
        Component title = Component.text("Settings: ", NamedTextColor.DARK_GRAY)
                .append(Component.text(name, NamedTextColor.DARK_PURPLE, TextDecoration.BOLD));

        this.inventory = Bukkit.createInventory(this, 27, title);
        refreshInventory();
    }

    public OfflinePlayer getTargetPlayer() {
        return targetPlayer;
    }

    public UserSettingsData getSettings() {
        return settings;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void refreshInventory() {
        inventory.clear();

        // Background filler
        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text(" "), null);
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, filler);
        }

        // Slot 10: Hotbar Toggle
        boolean keepHotbar = settings.isKeepHotbar();
        Material hotbarMat = keepHotbar ? Material.DIAMOND_SWORD : Material.WOODEN_SWORD;
        List<Component> hotbarLore = new ArrayList<>();
        hotbarLore.add(Component.text("Status: ", NamedTextColor.GRAY)
                .append(keepHotbar ? Component.text("ENABLED (Keep Hotbar)", NamedTextColor.GREEN) : Component.text("DISABLED (Drop Hotbar)", NamedTextColor.RED)));
        hotbarLore.add(Component.empty());
        hotbarLore.add(Component.text("Click to toggle", NamedTextColor.YELLOW));
        inventory.setItem(10, createItem(hotbarMat, Component.text("Hotbar Retention", NamedTextColor.GOLD, TextDecoration.BOLD), hotbarLore));

        // Slot 12: Backpack Keep Mode
        BackpackKeepMode mode = settings.getBackpackKeepMode();
        List<Component> modeLore = new ArrayList<>();
        modeLore.add(Component.text("Mode: ", NamedTextColor.GRAY)
                .append(Component.text(mode.getDisplayName(), NamedTextColor.AQUA, TextDecoration.BOLD)));
        modeLore.add(Component.empty());
        modeLore.add(Component.text("Click to cycle mode", NamedTextColor.YELLOW));
        inventory.setItem(12, createItem(Material.CHEST, Component.text("Backpack Retention Mode", NamedTextColor.GOLD, TextDecoration.BOLD), modeLore));

        // Slot 13: Backpack Keep Count
        int count = settings.getBackpackKeepCount();
        List<Component> countLore = new ArrayList<>();
        countLore.add(Component.text("Items Kept: ", NamedTextColor.GRAY)
                .append(Component.text(count + " / 27", NamedTextColor.GREEN, TextDecoration.BOLD)));
        countLore.add(Component.empty());
        countLore.add(Component.text("Left-Click: ", NamedTextColor.YELLOW).append(Component.text("+1 Item", NamedTextColor.WHITE)));
        countLore.add(Component.text("Right-Click: ", NamedTextColor.YELLOW).append(Component.text("-1 Item", NamedTextColor.WHITE)));
        inventory.setItem(13, createItem(Material.HOPPER, Component.text("Backpack Item Count", NamedTextColor.GOLD, TextDecoration.BOLD), countLore));

        // Slot 15: EXP Loss Type
        ExpLossType expType = settings.getExpLossType();
        List<Component> expTypeLore = new ArrayList<>();
        expTypeLore.add(Component.text("Type: ", NamedTextColor.GRAY)
                .append(Component.text(expType.getDisplayName(), NamedTextColor.LIGHT_PURPLE, TextDecoration.BOLD)));
        expTypeLore.add(Component.empty());
        expTypeLore.add(Component.text("Click to toggle EXP loss type", NamedTextColor.YELLOW));
        inventory.setItem(15, createItem(Material.EXPERIENCE_BOTTLE, Component.text("EXP Loss Type", NamedTextColor.GOLD, TextDecoration.BOLD), expTypeLore));

        // Slot 16: EXP Loss Percentage
        double pct = settings.getExpLossPercentage();
        List<Component> pctLore = new ArrayList<>();
        pctLore.add(Component.text("EXP Lost: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.0f%%", pct), NamedTextColor.RED, TextDecoration.BOLD)));
        pctLore.add(Component.text("EXP Kept: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.0f%%", 100.0 - pct), NamedTextColor.GREEN, TextDecoration.BOLD)));
        pctLore.add(Component.empty());
        pctLore.add(Component.text("Left-Click: ", NamedTextColor.YELLOW).append(Component.text("+10% Loss", NamedTextColor.WHITE)));
        pctLore.add(Component.text("Right-Click: ", NamedTextColor.YELLOW).append(Component.text("-10% Loss", NamedTextColor.WHITE)));
        inventory.setItem(16, createItem(Material.REDSTONE, Component.text("EXP Loss Percentage", NamedTextColor.GOLD, TextDecoration.BOLD), pctLore));

        // Slot 3: Keep Armor Toggle
        boolean keepArmor = settings.isKeepArmor();
        List<Component> keepArmorLore = new ArrayList<>();
        keepArmorLore.add(Component.text("Status: ", NamedTextColor.GRAY)
                .append(keepArmor ?
                        Component.text("ENABLED (Armor is kept)", NamedTextColor.GREEN, TextDecoration.BOLD) :
                        Component.text("DISABLED (Armor drops)", NamedTextColor.RED, TextDecoration.BOLD)));
        keepArmorLore.add(Component.empty());
        keepArmorLore.add(Component.text("Click: ", NamedTextColor.YELLOW)
                .append(Component.text("Toggle Status", NamedTextColor.WHITE)));
        inventory.setItem(3, createItem(Material.ANVIL, Component.text("Keep Armor on Death", NamedTextColor.GOLD, TextDecoration.BOLD), keepArmorLore));

        // Slot 4: Armor Damage Percentage
        double armorDmg = settings.getArmorDamagePercentage();
        List<Component> armorDmgLore = new ArrayList<>();
        armorDmgLore.add(Component.text("Durability Lost: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.0f%%", armorDmg), NamedTextColor.RED, TextDecoration.BOLD)));
        armorDmgLore.add(Component.text("Durability Kept: ", NamedTextColor.GRAY)
                .append(Component.text(String.format("%.0f%%", 100.0 - armorDmg), NamedTextColor.GREEN, TextDecoration.BOLD)));
        armorDmgLore.add(Component.empty());
        armorDmgLore.add(Component.text("Left-Click: ", NamedTextColor.YELLOW)
                .append(Component.text("+1% Damage (Max 25%)", NamedTextColor.WHITE)));
        armorDmgLore.add(Component.text("Right-Click: ", NamedTextColor.YELLOW)
                .append(Component.text("-1% Damage (Min 0%)", NamedTextColor.WHITE)));
        inventory.setItem(4, createItem(Material.FLINT, Component.text("Armor Damage Percentage", NamedTextColor.GOLD, TextDecoration.BOLD), armorDmgLore));

        // Slot 22: Back to Player List
        List<Component> backLore = new ArrayList<>();
        backLore.add(Component.text("Return to player selection", NamedTextColor.GRAY));
        inventory.setItem(22, createItem(Material.BARRIER, Component.text("Back to Player List", NamedTextColor.RED, TextDecoration.BOLD), backLore));
    }

    private ItemStack createItem(Material material, Component name, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            if (lore != null) {
                meta.lore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
