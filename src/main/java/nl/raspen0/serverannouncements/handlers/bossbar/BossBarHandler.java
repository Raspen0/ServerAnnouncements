package nl.raspen0.serverannouncements.handlers.bossbar;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.NoticeSendEvent;
import nl.raspen0.serverannouncements.events.NoticeType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarHandler {

    private final ServerAnnouncements plugin;
    private final int delay;
    private final int repeat;
    private final int duration;
    private final BossBar.Color color;
    private Map<UUID, BossBarTask> playerMap;

    public BossBarHandler(ServerAnnouncements plugin) {
        this.plugin = plugin;
        playerMap = new HashMap<>();
        delay = plugin.getConfig().getInt("notification.bossbar.delay");
        repeat = plugin.getConfig().getInt("notification.bossbar.repeat");
        duration = plugin.getConfig().getInt("notification.bossbar.duration");
        color = BossBar.Color.valueOf(plugin.getConfig().getString("notification.bossbar.color").toUpperCase());
    }

    public int startBossBarTask(Player player) {
        if (repeat > 0) {
            plugin.getPluginLogger().logDebug("Starting repeating BossBar task!");
            return repeatingBossBarTask(player);
        } else {
            plugin.getPluginLogger().logDebug("Starting BossBar task!");
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

        Component message = plugin.getLangHandler().getMessage(player, "noticeBossBar")
                .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(String.valueOf(event.getUnreadCount())).build());
        BossBar bar = BossBar.bossBar(message, 1, color, BossBar.Overlay.NOTCHED_10);
        Audience audience = ServerAnnouncements.getAudiences().player(player);
        audience.showBossBar(bar);
        int timerDuration = duration / 10;
        playerMap.put(player.getUniqueId(), new BossBarTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (bar.progress() < 0.1F) {
                bar.progress(0.0F);
                playerMap.get(player.getUniqueId()).clearBossbarTask();
                playerMap.remove(player.getUniqueId());
                //Half the first and last segment so the bossbar doesn't stay on screen an extra second.
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> audience.hideBossBar(bar), 10 * timerDuration);
                return;
            }
            bar.progress(bar.progress() - 0.1F);
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
        for(Map.Entry<UUID, BossBarTask> e : playerMap.entrySet()){
            UUID uuid = e.getKey();
            unloadPlayer(Bukkit.getPlayer(uuid));
        }
    }
}
