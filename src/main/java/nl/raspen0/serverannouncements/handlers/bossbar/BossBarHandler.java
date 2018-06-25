package nl.raspen0.serverannouncements.handlers.bossbar;

import nl.raspen0.serverannouncements.events.NoticeType;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.NoticeSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarHandler {

    private final ServerAnnouncements plugin;
    private final int delay;
    private final int repeat;
    private final int duration;
    private final BarColor color;
    private Map<UUID, BossBarTask> playerMap;

    public BossBarHandler(ServerAnnouncements plugin) {
        this.plugin = plugin;
        playerMap = new HashMap<>();
        delay = plugin.getConfig().getInt("notification.bossbar.delay");
        repeat = plugin.getConfig().getInt("notification.bossbar.repeat");
        duration = plugin.getConfig().getInt("notification.bossbar.duration");
        color = BarColor.valueOf(plugin.getConfig().getString("notification.bossbar.color").toUpperCase());
    }

    public int startBossBarTask(Player player) {
        if (repeat > 0) {
            return repeatingBossBarTask(player);
        } else {
            return bossBarTask(player);
        }
    }

    private int bossBarTask(Player player) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, () -> doBossBarTask(player), delay * 20).getTaskId();
    }

    private int repeatingBossBarTask(Player player) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> doBossBarTask(player), delay * 20, repeat * 20).getTaskId();
    }

    private void doBossBarTask(Player player) {
        NoticeSendEvent event = new NoticeSendEvent(player, plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getUnreadCount(),
                NoticeType.BOSSBAR);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()){
            return;
        }
        if (playerMap.containsKey(player.getUniqueId())) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error starting BossBar task, there is already a task running!");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please increase the delay between repeating of the task or decrease the duration of the task!");
            return;
        }
        BossBar bar = Bukkit.createBossBar(plugin.getLangHandler().getMessage(player, "noticeBossBar").replace("{0}",
                String.valueOf(event.getUnreadCount())), color, BarStyle.SEGMENTED_10);
        bar.addPlayer(player);
        int timerDuration = duration / 10;
        playerMap.put(player.getUniqueId(), new BossBarTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (bar.getProgress() < 0.1) {
                bar.setProgress(0.0);
                playerMap.get(player.getUniqueId()).clearBossbarTask();
                playerMap.remove(player.getUniqueId());
                //Half the first and last segment so the bossbar doesn't stay on screen an extra second.
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> bar.removePlayer(player), 10 * timerDuration);
                return;
            }
            bar.setProgress(bar.getProgress() - 0.1);
        }, 10 * timerDuration, 20 * timerDuration).getTaskId(), bar));
    }

    public void unloadPlayer(Player player){
        UUID uuid = player.getUniqueId();
        if(playerMap.containsKey(uuid)) {
            playerMap.get(uuid).clearBossbarTask();
            playerMap.get(uuid).removeBossBar(player);
            playerMap.remove(uuid);
        }
    }

    public void unloadPlayers(){
        for(Map.Entry e : playerMap.entrySet()){
            UUID uuid = (UUID) e.getKey();
            unloadPlayer(Bukkit.getPlayer(uuid));
        }
    }
}
