package mw.wowkfccc.TISF.tISF_UHC;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReviveManager {
    private final JavaPlugin plugin;
    private final File dataFile;
    private final YamlConfiguration yml;
    private final Map<UUID, Boolean> usedRevive = new HashMap<>();

    public ReviveManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "reviveData.yml");
        if (!dataFile.exists()) {
            plugin.saveResource("reviveData.yml", false);
        }
        this.yml = YamlConfiguration.loadConfiguration(dataFile);
        loadData();
    }
    private void loadData() {
        for (String key : yml.getConfigurationSection("players").getKeys(false)) {
            usedRevive.put(UUID.fromString(key), yml.getBoolean("players." + key + ".revived"));
        }
    }
    public boolean hasUsed(UUID uuid) {
        return usedRevive.getOrDefault(uuid, false);
    }
    public void markUsed(UUID uuid) {
        usedRevive.put(uuid, true);
        yml.set("players." + uuid.toString() + ".revived", true);
    }
    public void saveData() {
        try {
            yml.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("儲存 reviveData.yml 失敗: " + e.getMessage());
        }
    }
}

