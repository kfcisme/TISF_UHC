package mw.wowkfccc.TISF.tISF_UHC;

import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getLogger;

public class TISF_UHC extends JavaPlugin {
    private ReviveManager reviveManager;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) getDataFolder().mkdirs();

        reviveManager = new ReviveManager(this);

        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this, reviveManager), this);

        getLogger().info("TISF_UHC 已啟動！");
    }

    @Override
    public void onDisable() {
        reviveManager.saveData();
        getLogger().info("TISF_UHC 已關閉，資料已儲存。");
    }

    public ReviveManager getReviveManager() {
        return reviveManager;
    }
}

