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

        // 2. Load defaults into your static variables
        loadDefaultsToStatics();

        // 3. Keep your data manager initialized
        this.settingsManager = new PlayerSettingsManager(this);

        // 4. Keep all of your background event listeners working perfectly
        getServer().getPluginManager().registerEvents(new DeathEventListener(settingsManager), this);
        getServer().getPluginManager().registerEvents(new GuiListener(settingsManager), this);

        // 5. Initialize the command instance with its required manager
        AdminCommand adminCommand = new AdminCommand(this.settingsManager);

        // 6. Fixed registration utilizing the proper 26.2 command routing
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

    public void loadDefaultsToStatics() {
        FileConfiguration config = getConfig();

        UserSettingsData.DEFAULT_KEEP_HOTBAR = config.getBoolean("default-settings.keep-hotbar", false);
        UserSettingsData.DEFAULT_BACKPACK_COUNT = config.getInt("default-settings.backpack-keep-count", 0);
        UserSettingsData.DEFAULT_EXP_PERCENTAGE = config.getDouble("default-settings.exp-loss-percentage", 100.0);
        UserSettingsData.DEFAULT_KEEP_ARMOR = config.getBoolean("default-settings.keep-armor", false);
        UserSettingsData.DEFAULT_ARMOR_DAMAGE_PERCENTAGE = config.getDouble("default-settings.armor-damage-percentage", 0.0);

        try {
            UserSettingsData.DEFAULT_BACKPACK_MODE = BackpackKeepMode.valueOf(config.getString("default-settings.backpack-keep-mode", "KEEP_NONE"));
        } catch (IllegalArgumentException e) {
            UserSettingsData.DEFAULT_BACKPACK_MODE = BackpackKeepMode.KEEP_NONE;
        }

        try {
            UserSettingsData.DEFAULT_EXP_LOSS_TYPE = ExpLossType.valueOf(config.getString("default-settings.exp-loss-type", "ALL_LEVELS"));
        } catch (IllegalArgumentException e) {
            UserSettingsData.DEFAULT_EXP_LOSS_TYPE = ExpLossType.ALL_LEVELS;
        }
    }
}
