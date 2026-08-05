package ca.pebn.usersettings;

import ca.pebn.usersettings.command.AdminCommand;
import ca.pebn.usersettings.gui.GuiListener;
import ca.pebn.usersettings.listener.DeathEventListener;
import ca.pebn.usersettings.storage.PlayerSettingsManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserSettings extends JavaPlugin {

    private PlayerSettingsManager settingsManager;

    @Override
    public void onEnable() {
        // 1. Keep your file management intact
        saveDefaultConfig();

        // 2. Keep your data manager initialized
        this.settingsManager = new PlayerSettingsManager(this);

        // 3. Keep all of your background event listeners working perfectly
        getServer().getPluginManager().registerEvents(new DeathEventListener(settingsManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(settingsManager), this);

        // 4. Initialize the command instance with its required manager
        AdminCommand adminCommand = new AdminCommand(this.settingsManager);

        // 5. Fixed registration utilizing the proper 26.2 command routing
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final io.papermc.paper.command.brigadier.Commands commands = event.registrar();
            
            // Registers your literal node builder or BasicCommand wrapper cleanly
            commands.register(adminCommand.getCommandNode());
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
