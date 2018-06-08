package nl.raspen0.serverannouncements.handlers.bossbar;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarHandler {

    private final ServerAnnouncements plugin;
    private int delay;
    private int repeat;
    private int duration;
    private BarColor color;

    public BossBarHandler(ServerAnnouncements plugin) {
        this.plugin = plugin;
        delay = plugin.getConfig().getInt("notification.bossbar.delay");
        repeat = plugin.getConfig().getInt("notification.bossbar.repeat");
        duration = plugin.getConfig().getInt("notification.bossbar.duration");
        color = BarColor.valueOf(plugin.getConfig().getString("notification.bossbar.color").toUpperCase());
    }

    public int startBossBarTask(Player player, String count) {
        if (repeat > 0) {
            return repeatingBossBarTask(player, count);
        } else {
            return bossBarTask(player, count);
        }
    }

    private int bossBarTask(Player player, String count) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, () -> doBossBarTask(player, count), delay * 20).getTaskId();
    }

    private int repeatingBossBarTask(Player player, String count) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> doBossBarTask(player, count), delay * 20, repeat * 20).getTaskId();
    }

    private void doBossBarTask(Player player, String count) {
        if(plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getBossbarTask() != 0){
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Error starting BossBar task, there is already a task running!");
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Please increase the delay between repeating of the task or decrease the duration of the task!");
            return;
        }
        BossBar bar = Bukkit.createBossBar(plugin.getLangHandler().getMessage("noticeBossBar").replace("{0}", count), color, BarStyle.SEGMENTED_10);
        bar.addPlayer(player);
        int timerDuration = duration / 10;
        plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setBossbarTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if(bar.getProgress() < 0.1){
                bar.setProgress(0.0);
                plugin.getPlayerHandler().getPlayer(player.getUniqueId()).clearBossbarTask();
                //Half the first and last segment so the bossbar doesn't stay on screen an extra second.
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> bar.removePlayer(player), 10 * timerDuration);
                return;
            }
            bar.setProgress(bar.getProgress() - 0.1);
        }, 10 * timerDuration, 20 * timerDuration).getTaskId());
    }
}
