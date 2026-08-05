package ca.pebn.usersettings.command;

import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.gui.PlayerSelectorGui;
import ca.pebn.usersettings.gui.UserSettingsEditorGui;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyri.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AdminCommand {

    private final PlayerSettingsManager settingsManager;

    public AdminCommand(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    /**
     * Creates a BasicCommand instance for registration with Paper 26.2's brigadier system.
     */
    public io.papermc.paper.command.brigadier.BasicCommand toBasic() {
        var editCommand = Commands.literal("edit")
                .requires(source -> source.hasPermission("usersettings.admin"))
                .argument(Commands.stringArgument("targetPlayer", (context, sender) -> {
                    String playerName = context.getOrDefault("targetPlayer");
                    
                    if (!(sender instanceof Player player)) {
                        PaperAdventure.broadcast(Component.text("Only players can open the GUI editor.", NamedTextColor.RED));
                        return 0;
                    }

                    OfflinePlayer target = findOfflinePlayer(playerName);
                    UserSettingsData data = settingsManager.getSettings(target);
                    player.openInventory(new UserSettingsEditorGui(target, data).getInventory());
                    
                    PaperAdventure.sender(sender).sendMessage(Component.text("Opening editor for " + playerName));
                    return 1;
                }));

        var rootCommand = Commands.literal("usersettings")
                .requires(source -> source.hasPermission("usersettings.admin"))
                .executes((context, sender) -> {
                    if (!(sender instanceof Player player)) {
                        PaperAdventure.sender(sender).sendMessage(Component.text("Usage: /usersettes edit <player>", NamedTextColor.RED));
                        return 0;
                    }

                    player.openInventory(new PlayerSelectorGui(settingsManager).getInventory());
                    return 1;
                })
                .then(editCommand);

        return rootCommand.build();
    }

    private OfflinePlayer findOfflinePlayer(String playerName) {
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(playerName);
        if (target == null || !target.hasPlayedBefore()) {
            target = Bukkit.getPlayerExact(playerName);
            if (target == null || !target.isOnline()) {
                target = Bukkit.getOfflinePlayer(playerName);
            }
        }
        return target;
    }

}
