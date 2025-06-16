// File: PlayerChunkLoadListener.java
package me.wowkfccc.logplayeraction.logplayeraction.event;

import me.wowkfccc.logplayeraction.logplayeraction.Logplayeraction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerChunkLoadListener implements Listener {
    public final Logplayeraction plugin;
    public static final Map<UUID, Integer> chunkLoadCounts = new HashMap<>();
    public final int viewDistance;
    public final int theoreticalChunkCount;

    public PlayerChunkLoadListener(Logplayeraction plugin) {
        this.plugin = plugin;
        // Read view-distance from server.properties
        this.viewDistance = Bukkit.getServer().getViewDistance();
        // Calculate (2R+1)^2
        this.theoreticalChunkCount = (2 * viewDistance + 1) * (2 * viewDistance + 1);

        plugin.getLogger().info("Server view-distance = " + viewDistance
                + ", theoretical chunk count per load = " + theoreticalChunkCount);


    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Chunk fromChunk = event.getFrom().getChunk();
        Chunk toChunk = event.getTo().getChunk();
        // Only count if player crosses into a different chunk
        if (fromChunk.equals(toChunk)) return;

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        int count = chunkLoadCounts.getOrDefault(playerId, 0) + 1;
        chunkLoadCounts.put(playerId, count);

        plugin.getLogger().info("Player " + player.getName()
                + " chunk crosses = " + count
                + ", view-distance = " + viewDistance
                + ", theoretical chunk count = " + theoreticalChunkCount);
    }

    public static int SendInsertData(UUID playerId){
        return chunkLoadCounts.getOrDefault(playerId, 0);
    }

    public static void resetChunkLoadCounts(UUID playerId) {
        // Log the counters before resetting
        for (Map.Entry<UUID, Integer> entry : chunkLoadCounts.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

//            }
        }
        chunkLoadCounts.remove(playerId);
        Bukkit.getLogger().info("All player chunk load counts have been reset.");
    }
}
