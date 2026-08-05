package ca.pebn.usersettings.command;

import ca.pebn.usersettings.data.UserSettingsData;
import ca.pebn.usersettings.gui.PlayerSelectorGui;
import ca.pebn.usersettings.gui.UserSettingsEditorGui;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class AdminCommand {

    private final PlayerSettingsManager settingsManager;

    public AdminCommand(PlayerSettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    /**
     * Compiles the command tree into a LiteralCommandNode acceptable by Paper 26.2's Registrar.
     */
    public LiteralCommandNode<CommandSourceStack> getCommandNode() {
        
        var editCommand = Commands.literal("edit")
                .requires(source -> source.getSender().hasPermission("usersettings.admin"))
                .then(Commands.argument("targetPlayer", StringArgumentType.string())
                        .executes(context -> {
                            // Extract command values cleanly out of the Brigadier context
                            CommandSourceStack source = context.getSource();
                            String playerName = context.getArgument("targetPlayer", String.class);
                            
                            if (!(source.getSender() instanceof Player player)) {
                                source.getSender().sendMessage(Component.text("Only players can open the GUI editor.", NamedTextColor.RED));
                                return 0;
                            }

                            OfflinePlayer target = findOfflinePlayer(playerName);
                            UserSettingsData data = settingsManager.getSettings(target);
                            player.openInventory(new UserSettingsEditorGui(target, data).getInventory());
                            
                            source.getSender().sendMessage(Component.text("Opening editor for " + playerName, NamedTextColor.GREEN));
                            return 1;
                        }));

        var rootCommand = Commands.literal("usersettings")
                .requires(source -> source.getSender().hasPermission("usersettings.admin"))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    
                    if (!(source.getSender() instanceof Player player)) {
                        source.getSender().sendMessage(Component.text("Usage: /usersettings edit <player>", NamedTextColor.RED));
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
