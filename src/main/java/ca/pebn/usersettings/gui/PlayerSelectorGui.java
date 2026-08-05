package ca.pebn.usersettings.gui;

import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectorGui implements InventoryHolder {

    private final PlayerSettingsManager settingsManager;
    private final Inventory inventory;
    private int page = 0;
    private List<OfflinePlayer> players = new ArrayList<>();

    public PlayerSelectorGui(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.inventory = Bukkit.createInventory(this, 54, Component.text("Select Player to Edit", NamedTextColor.DARK_BLUE, TextDecoration.BOLD));
        refresh();
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
        refresh();
    }

    public List<OfflinePlayer> getPlayers() {
        return players;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void refresh() {
        inventory.clear();
        this.players = settingsManager.getAllKnownPlayers();

        int pageSize = 45;
        int totalPages = (int) Math.ceil((double) players.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page >= totalPages) page = totalPages - 1;

        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, players.size());

        for (int i = startIndex; i < endIndex; i++) {
            OfflinePlayer op = players.get(i);
            int slot = i - startIndex;
            inventory.setItem(slot, createPlayerHead(op));
        }

        // Bottom row controls (slots 45-53)
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        for (int s = 45; s < 54; s++) {
            inventory.setItem(s, filler);
        }

        if (page > 0) {
            ItemStack prev = new ItemStack(Material.ARROW);
            prev.editMeta(meta -> meta.displayName(Component.text("Previous Page", NamedTextColor.YELLOW)));
            inventory.setItem(48, prev);
        }

        final int currentPageDisplay = page + 1;
        final int totalPagesDisplay = totalPages;
        ItemStack info = new ItemStack(Material.BOOK);
        info.editMeta(meta -> meta.displayName(Component.text("Page " + currentPageDisplay + " of " + totalPagesDisplay, NamedTextColor.GOLD)));
        inventory.setItem(49, info);

        if (page < totalPages - 1) {
            ItemStack next = new ItemStack(Material.ARROW);
            next.editMeta(meta -> meta.displayName(Component.text("Next Page", NamedTextColor.YELLOW)));
            inventory.setItem(50, next);
        }
    }

    private ItemStack createPlayerHead(OfflinePlayer op) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        if (meta != null) {
            meta.setOwningPlayer(op);
            String name = op.getName() != null ? op.getName() : "Unknown Player";
            meta.displayName(Component.text(name, op.isOnline() ? NamedTextColor.GREEN : NamedTextColor.GRAY, TextDecoration.BOLD));

            UserSettingsData data = settingsManager.getSettings(op.getUniqueId());
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("Status: ", NamedTextColor.GRAY)
                    .append(op.isOnline() ? Component.text("ONLINE", NamedTextColor.GREEN) : Component.text("OFFLINE", NamedTextColor.RED)));
            lore.add(Component.empty());
            lore.add(Component.text("Hotbar: ", NamedTextColor.GRAY)
                    .append(data.isKeepHotbar() ? Component.text("Keep", NamedTextColor.GREEN) : Component.text("Drop", NamedTextColor.RED)));
            lore.add(Component.text("Backpack: ", NamedTextColor.GRAY)
                    .append(Component.text(data.getBackpackKeepMode().getDisplayName(), NamedTextColor.AQUA)));
            lore.add(Component.text("EXP Loss: ", NamedTextColor.GRAY)
                    .append(Component.text(String.format("%.0f%% (%s)", data.getExpLossPercentage(), data.getExpLossType().getDisplayName()), NamedTextColor.LIGHT_PURPLE)));
            lore.add(Component.empty());
            lore.add(Component.text("Click to edit settings", NamedTextColor.YELLOW));
            meta.lore(lore);
            head.setItemMeta(meta);
        }
        return head;
    }
}
