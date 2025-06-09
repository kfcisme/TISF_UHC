package me.wowkfccc.logplayeraction.logplayeraction.event;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.wowkfccc.logplayeraction.logplayeraction.Logplayeraction;

public class onRedstoneTracker implements Listener{
    private final Logplayeraction plugin;
    public static final Map<UUID, Integer> playerRedstoneCount = new HashMap<>();
    private static final Set<Material> redstoneMaterials = EnumSet.of(
            Material.REDSTONE_WIRE,
            Material.REDSTONE_TORCH,
//            Material.UNLIT_REDSTONE_TORCH,
            Material.REPEATER,
            Material.COMPARATOR,
            Material.REDSTONE_BLOCK,
            Material.RAIL,
            Material.DETECTOR_RAIL,
            Material.ACTIVATOR_RAIL,
            Material.POWERED_RAIL,
            Material.REDSTONE,
            Material.HOPPER,
            Material.DROPPER,
            Material.DISPENSER,
            Material.PISTON,
            Material.STICKY_PISTON,
            Material.TARGET,
            Material.LEVER,
            Material.OAK_PRESSURE_PLATE,
            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.REDSTONE_LAMP,
            Material.OAK_DOOR,
            Material.IRON_DOOR,
            Material.OBSERVER,
            Material.DAYLIGHT_DETECTOR,
            Material.BELL,
            Material.LECTERN,
            Material.MINECART,
            Material.CHEST_MINECART,
            Material.FURNACE_MINECART,
            Material.TNT_MINECART,
            Material.HOPPER_MINECART,
            Material.TRAPPED_CHEST,
            Material.CHEST,
            Material.BARREL,
            Material.TRIPWIRE_HOOK,
            Material.TRIPWIRE,
            Material.SLIME_BLOCK,
            Material.OAK_FENCE_GATE,
            Material.NOTE_BLOCK,
            Material.OAK_TRAPDOOR,
            Material.IRON_TRAPDOOR,
            Material.BAMBOO_TRAPDOOR,
            Material.POLISHED_BLACKSTONE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.STONE_BUTTON
    );

    public onRedstoneTracker(Logplayeraction plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (redstoneMaterials.contains(event.getBlockPlaced().getType())) {
            playerRedstoneCount.put(playerId, playerRedstoneCount.getOrDefault(playerId, 0) + 1);
            Bukkit.getLogger().info("Player " + player.getName() + " placed a redstone block. Total count: " + playerRedstoneCount.get(playerId));
        }
    }

    public static int SendInsertData(UUID playerId) {
        return playerRedstoneCount.getOrDefault(playerId, 0);
    }
    public static void resetCounters(UUID playerId) {
        // Log the counters before resetting
        Player player = Bukkit.getPlayer(playerId);
        for (Map.Entry<UUID, Integer> entry : playerRedstoneCount.entrySet()) {
            //Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                Bukkit.getLogger().info("Player " + player.getName() + " total redstone block count: " + entry.getValue());
            }
        }
        // Clear the counters
        playerRedstoneCount.remove(playerId);
        Bukkit.getLogger().info("All player redstone block counters have been reset.");
    }
}
