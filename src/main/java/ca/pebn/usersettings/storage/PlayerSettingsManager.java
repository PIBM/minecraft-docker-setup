package ca.pebn.usersettings.storage;

import ca.pebn.usersettings.data.BackpackKeepMode;
import ca.pebn.usersettings.data.ExpLossType;
import ca.pebn.usersettings.data.UserSettingsData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerSettingsManager {

    private final JavaPlugin plugin;
    private final File playersFolder;
    private final Map<UUID, UserSettingsData> cache = new ConcurrentHashMap<>();

    public PlayerSettingsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.playersFolder = new File(plugin.getDataFolder(), "players");
        if (!playersFolder.exists()) {
            playersFolder.mkdirs();
        }
    }

    public UserSettingsData getSettings(UUID playerUuid) {
        return cache.computeIfAbsent(playerUuid, this::loadSettings);
    }

    public UserSettingsData getSettings(OfflinePlayer offlinePlayer) {
        UserSettingsData data = getSettings(offlinePlayer.getUniqueId());
        if (offlinePlayer.getName() != null) {
            data.setLastKnownName(offlinePlayer.getName());
        }
        return data;
    }

    private UserSettingsData loadSettings(UUID playerUuid) {
        File file = new File(playersFolder, playerUuid.toString() + ".yml");
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUuid);
        String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";

        UserSettingsData data = new UserSettingsData(playerUuid, name);

        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            data.setLastKnownName(config.getString("last-known-name", name));
            data.setKeepHotbar(config.getBoolean("keep-hotbar", false));

            String backpackModeStr = config.getString("backpack-keep-mode", "KEEP_NONE");
            try {
                data.setBackpackKeepMode(BackpackKeepMode.valueOf(backpackModeStr));
            } catch (IllegalArgumentException e) {
                data.setBackpackKeepMode(BackpackKeepMode.KEEP_NONE);
            }

            data.setBackpackKeepCount(config.getInt("backpack-keep-count", 0));

            String expLossTypeStr = config.getString("exp-loss-type", "ALL_LEVELS");
            try {
                data.setExpLossType(ExpLossType.valueOf(expLossTypeStr));
            } catch (IllegalArgumentException e) {
                data.setExpLossType(ExpLossType.ALL_LEVELS);
            }

            data.setExpLossPercentage(config.getDouble("exp-loss-percentage", 100.0));
        }

        return data;
    }

    public void saveSettings(UserSettingsData data) {
        File file = new File(playersFolder, data.getPlayerUuid().toString() + ".yml");
        YamlConfiguration config = new YamlConfiguration();

        config.set("last-known-name", data.getLastKnownName());
        config.set("keep-hotbar", data.isKeepHotbar());
        config.set("backpack-keep-mode", data.getBackpackKeepMode().name());
        config.set("backpack-keep-count", data.getBackpackKeepCount());
        config.set("exp-loss-type", data.getExpLossType().name());
        config.set("exp-loss-percentage", data.getExpLossPercentage());

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save settings for player " + data.getLastKnownName() + ": " + e.getMessage());
        }
    }

    public List<OfflinePlayer> getAllKnownPlayers() {
        Set<UUID> uuids = new HashSet<>();

        // Add online players
        Bukkit.getOnlinePlayers().forEach(p -> uuids.add(p.getUniqueId()));

        // Add saved players from directory
        File[] files = playersFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                try {
                    String uuidStr = fileName.substring(0, fileName.length() - 4);
                    uuids.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        List<OfflinePlayer> players = new ArrayList<>();
        for (UUID uuid : uuids) {
            players.add(Bukkit.getOfflinePlayer(uuid));
        }

        players.sort((p1, p2) -> {
            boolean p1Online = p1.isOnline();
            boolean p2Online = p2.isOnline();
            if (p1Online != p2Online) {
                return p1Online ? -1 : 1;
            }
            String n1 = p1.getName() != null ? p1.getName() : "";
            String n2 = p2.getName() != null ? p2.getName() : "";
            return n1.compareToIgnoreCase(n2);
        });

        return players;
    }
}
