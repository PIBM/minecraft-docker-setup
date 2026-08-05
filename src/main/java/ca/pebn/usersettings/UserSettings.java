package ca.pebn.usersettings;

import ca.pebn.usersettings.command.AdminCommand;
import ca.pebn.usersettings.gui.GuiListener;
import ca.pebn.usersettings.listener.DeathEventListener;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class UserSettings extends JavaPlugin {

    private PlayerSettingsManager settingsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.settingsManager = new PlayerSettingsManager(this);

        // Register Event Listeners
        getServer().getPluginManager().registerEvents(new DeathEventListener(settingsManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(settingsManager), this);

        // Register Admin Command via Paper 26.2 LifecycleManager
        AdminCommand adminCommand = new AdminCommand(settingsManager);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("usersettings", "Admin command to edit per-user death settings", List.of("settings", "deathsettings"), adminCommand);
        });

        getLogger().info("UserSettings plugin for Paper 26.2 has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UserSettings plugin has been disabled.");
    }

    public PlayerSettingsManager getSettingsManager() {
        return settingsManager;
    }
}
