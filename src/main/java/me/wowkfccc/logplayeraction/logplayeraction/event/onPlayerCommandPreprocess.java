package me.wowkfccc.logplayeraction.logplayeraction.event;

import me.wowkfccc.logplayeraction.logplayeraction.Logplayeraction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class onPlayerCommandPreprocess implements Listener {
    private final Logplayeraction plugin;
    public static final Map<UUID, Integer> playerPlayerCommandPreprocessCount = new HashMap<>();

    public onPlayerCommandPreprocess(Logplayeraction plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        playerPlayerCommandPreprocessCount.put(playerId,
                playerPlayerCommandPreprocessCount.getOrDefault(playerId, 0) + 1);

        // Bukkit.getLogger().info("Player " + player.getName() + " command count: " +
        //     playerPlayerCommandPreprocessCount.get(playerId));
    }

    public static int SendInsertData(UUID playerId){
        return playerPlayerCommandPreprocessCount.getOrDefault(playerId, 0);
    }

    public static void resetCounters(UUID playerId) {
        playerPlayerCommandPreprocessCount.remove(playerId);
    }
}
