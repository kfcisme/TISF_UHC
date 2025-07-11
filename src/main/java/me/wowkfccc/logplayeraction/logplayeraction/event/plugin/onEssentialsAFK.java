package me.wowkfccc.logplayeraction.logplayeraction.event.plugin;

import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.wowkfccc.logplayeraction.logplayeraction.Logplayeraction;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class onEssentialsAFK implements Listener {
    private final Logplayeraction plugin;

    // 用 int (秒) 記錄進入 AFK 的時間戳
    public static final Map<UUID, Integer> afkStartSec = new HashMap<>();

    // 累計所有 AFK 時長（秒）
    public static final Map<UUID, Integer> afkTotalSec = new HashMap<>();

    public onEssentialsAFK(Logplayeraction plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAfkStatusChange(AfkStatusChangeEvent event) {
        UUID uuid = event.getAffected().getBase().getUniqueId();
        boolean isAfk = event.getValue();

        if (isAfk) {
            // 進入 AFK：記錄當前秒級時間戳
            int nowSec = (int)(System.currentTimeMillis() / 1000);
            afkStartSec.put(uuid, nowSec);
//            Bukkit.getLogger().info("Player " + event.getAffected().getBase().getName() + " enter AFK status" );


        } else {
            // 離開 AFK：計算這段 AFK 持續秒數並累加
            Integer start = afkStartSec.remove(uuid);
            if (start != null) {
                int nowSec = (int)(System.currentTimeMillis() / 1000);
                int duration = nowSec - start;
                int prev = afkTotalSec.getOrDefault(uuid, 0);
                afkTotalSec.put(uuid, prev + duration);
                plugin.getLogger().info(
                        event.getAffected().getBase().getName()
                                + " AFK " + duration + " 秒，累計 " + (prev + duration) + " 秒。"
                );
            }
        }
    }

    /**
     * 取得目前總 AFK 秒數。
     * 如果玩家正處於 AFK 中，會加上「從進入 AFK 到現在」的秒數。
     */
    public static int SendInsertData(UUID uuid) {
        int total = afkTotalSec.getOrDefault(uuid, 0);
        Integer start = afkStartSec.get(uuid);
        if (start != null) {
            int nowSec = (int)(System.currentTimeMillis() / 1000);
            total += (nowSec - start);
        }
        return total;
    }

    // 如果需要往資料庫 insert 時呼叫
    public static int t(UUID playerId) {
        return afkTotalSec.getOrDefault(playerId, 0);
    }

    // 重設某玩家的 AFK 計數
    public static void resetCounters(UUID playerId) {
        afkTotalSec.remove(playerId);
        afkStartSec.remove(playerId);
    }
}
