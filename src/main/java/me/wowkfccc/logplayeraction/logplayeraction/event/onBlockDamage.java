package me.wowkfccc.logplayeraction.logplayeraction.event;

import me.wowkfccc.logplayeraction.logplayeraction.Logplayeraction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class onBlockDamage implements Listener {
    private final Logplayeraction plugin;
    public static final Map<UUID, Integer> playerBlockDamageCount = new HashMap<>();

    public onBlockDamage(Logplayeraction plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Increment the player's block break counter
        playerBlockDamageCount.put(playerId, playerBlockDamageCount.getOrDefault(playerId, 0) + 1);

        // Log the current block break count for the player
        //Bukkit.getLogger().info("Player " + player.getName() + " Block Damage count: " + playerBlockDamageCount.get(playerId));
    }

    public static int SendInsertData(UUID playerId){
        return playerBlockDamageCount.getOrDefault(playerId, 0);
    }

    public static void resetCounters(UUID playerId) {
        // Log the counters before resetting
        Player player = Bukkit.getPlayer(playerId);
        for (Map.Entry<UUID, Integer> entry : playerBlockDamageCount.entrySet()) {
//            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                //Bukkit.getLogger().info("Player " + player.getName() + " total Block Damage count: " + entry.getValue());
            }
        }

        // Clear the counters
        playerBlockDamageCount.remove(playerId);
        //Bukkit.getLogger().info("All player Block Damage counters have been reset.");
    }
}
