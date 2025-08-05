package mw.wowkfccc.TISF.tISF_UHC;

import com.gmail.val59000mc.UhcCore;
import com.gmail.val59000mc.exceptions.UhcPlayerDoesNotExistException;
import com.gmail.val59000mc.exceptions.UhcPlayerNotOnlineException;
import com.gmail.val59000mc.game.GameManager;
import com.gmail.val59000mc.players.PlayerManager;
import com.gmail.val59000mc.players.TeamManager;
import com.gmail.val59000mc.players.UhcPlayer;
import com.gmail.val59000mc.players.UhcTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PlayerDeathListener implements Listener {
    private final JavaPlugin plugin;
    private final ReviveManager reviveManager;
    private final PlayerManager playerManager;
    private final TeamManager teamManager;

    public PlayerDeathListener(JavaPlugin plugin, ReviveManager reviveManager) {
        this.plugin = plugin;
        this.reviveManager = reviveManager;

        GameManager gm = GameManager.getGameManager();
        this.playerManager = gm.getPlayerManager();
        this.teamManager   = gm.getTeamManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID uuid = event.getEntity().getUniqueId();

        // 1. 如果已使用過復活機會，就跳過
        if (reviveManager.hasUsed(uuid)) {
            return;
        }

        // 2. 取得 UhcPlayer（處理不存在例外）
        UhcPlayer uhcPlayer;
        try {
            uhcPlayer = playerManager.getUhcPlayer(uuid);
        } catch (UhcPlayerDoesNotExistException ex) {
            return;
        }

        // 3. 取得所屬隊伍
        UhcTeam team = uhcPlayer.getTeam();
        if (team == null) return;

        // 4. 蒐集隊伍中「在線上、排除自己」的隊友
        List<UhcPlayer> aliveMates = new ArrayList<>();
        for (UhcPlayer up : team.getMembers()) {
            try {
                Player p2 = up.getPlayer();
                if (p2 != null && p2.isOnline() && !p2.getUniqueId().equals(uuid)) {
                    aliveMates.add(up);
                }
            } catch (UhcPlayerNotOnlineException ignore) {
                // 剛下線或例外，略過
            }
        }
        if (aliveMates.isEmpty()) return;

        // 5. 標記復活機會已用
        reviveManager.markUsed(uuid);

        // 6. 延遲20秒自動復活
        new BukkitRunnable() {
            @Override
            public void run() {
                Player p;
                // 確認玩家仍在線
                p = Bukkit.getPlayer(uuid);
                if (p == null || !p.isOnline()) return;

                // 隨機選一位活著隊友
                UhcPlayer mate = aliveMates.get(new Random().nextInt(aliveMates.size()));
                Location loc;
                try {
                    loc = mate.getPlayer().getLocation();
                } catch (UhcPlayerNotOnlineException e) {
                    return;
                }

                // 強制 respawn 並傳送到隊友身旁
                p.spigot().respawn();
                p.teleport(loc);

                // 再延遲0.5秒設定半血並顯示提示
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        double maxHp = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                        double halfHp = Math.max(1.0, maxHp / 2.0);
                        p.setHealth(halfHp);
                        p.sendTitle(
                                ChatColor.GOLD   + "你已被復活！",
                                ChatColor.YELLOW + "與隊友並肩作戰！",
                                10, 60, 10
                        );
                    }
                }.runTaskLater(plugin, 10L);
            }
        }.runTaskLater(plugin, 20 * 20L);

        // 7. 檢查剩 5 隊且都沒用過復活，給予 +2.5 心
        checkBonusHealth();
    }

    private void checkBonusHealth() {
        // 1. 收集所有「至少一名成員在線上」的隊伍
        List<UhcTeam> aliveTeams = new ArrayList<>();
        for (UhcTeam t : teamManager.getUhcTeams()) {
            boolean hasOnline = false;
            for (UhcPlayer up : t.getMembers()) {
                try {
                    Player p2 = up.getPlayer();
                    if (p2 != null && p2.isOnline()) {
                        hasOnline = true;
                        break;
                    }
                } catch (UhcPlayerNotOnlineException ignore) { }
            }
            if (hasOnline) aliveTeams.add(t);
        }

        // 2. 如果正好剩 5 隊，且這 5 隊所有在線上成員都還沒用過復活
        if (aliveTeams.size() == 5) {
            boolean allUnused = true;
            for (UhcTeam t : aliveTeams) {
                for (UhcPlayer up : t.getMembers()) {
                    try {
                        Player p2 = up.getPlayer();
                        if (p2 != null && p2.isOnline() && reviveManager.hasUsed(p2.getUniqueId())) {
                            allUnused = false;
                            break;
                        }
                    } catch (UhcPlayerNotOnlineException ignore) { }
                }
                if (!allUnused) break;
            }

            // 3. 全隊都未使用復活，給 Bonus
            if (allUnused) {
                for (UhcTeam t : aliveTeams) {
                    for (UhcPlayer up : t.getMembers()) {
                        Player p2;
                        try {
                            p2 = up.getPlayer();
                        } catch (UhcPlayerNotOnlineException e) {
                            continue;
                        }
                        if (p2.isOnline()) {
                            double curMax = p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                            p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(curMax + 5.0);
                            p2.sendMessage(
                                    ChatColor.GREEN + "Bonus：" +
                                            ChatColor.WHITE + "你的隊伍尚未使用復活，獲得額外 2.5 顆心！"
                            );
                        }
                    }
                }
            }
        }
    }
}
