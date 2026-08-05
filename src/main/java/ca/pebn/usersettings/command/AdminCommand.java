package ca.pebn.usersettings.command;

import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.gui.PlayerSelectorGui;
import ca.pebn.usersettings.gui.UserSettingsEditorGui;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AdminCommand implements CommandExecutor, TabCompleter {

    private final PlayerSettingsManager settingsManager;

    public AdminCommand(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("usersettings.admin")) {
            sender.sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Usage: /usersettings edit <player>", NamedTextColor.RED));
                return true;
            }
            player.openInventory(new PlayerSelectorGui(settingsManager).getInventory());
            return true;
        }

        if (args[0].equalsIgnoreCase("edit") && args.length >= 2) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Only players can open the GUI editor.", NamedTextColor.RED));
                return true;
            }

            String targetName = args[1];
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(targetName);
            if (target == null) {
                // Fallback to online or exact name lookup
                target = Bukkit.getPlayerExact(targetName);
                if (target == null) {
                    target = Bukkit.getOfflinePlayer(targetName);
                }
            }

            UserSettingsData data = settingsManager.getSettings(target);
            player.openInventory(new UserSettingsEditorGui(target, data).getInventory());
            return true;
        }

        sender.sendMessage(Component.text("Usage: /usersettings or /usersettings edit <player>", NamedTextColor.YELLOW));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("usersettings.admin")) {
            return List.of();
        }

        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            if ("edit".startsWith(args[0].toLowerCase())) {
                completions.add("edit");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            String partial = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
        }
        return completions;
    }
}
