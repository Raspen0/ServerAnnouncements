package nl.raspen0.serverannouncements.handlers.actionbar;

import net.kyori.adventure.text.TextReplacementConfig;
import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.NoticeSendEvent;
import nl.raspen0.serverannouncements.events.NoticeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ActionBarHandler {

    private final ServerAnnouncements plugin;
    private Map<UUID, ActionBarData> playerMap;
    private final int delay;
    private final int repeat;
    private final int duration;

    public ActionBarHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        playerMap = new HashMap<>();
        delay = plugin.getConfig().getInt("notification.actionbar.delay");
        repeat = plugin.getConfig().getInt("notification.actionbar.repeat");
        duration = plugin.getConfig().getInt("notification.actionbar.duration") - 2;
    }

    public int startActionBarTask(Player player){
        if(repeat > 0){
            plugin.getPluginLogger().logDebug("Starting repeating ActionBar task!");
            return repeatingActionBarTask(player);
        } else {
            plugin.getPluginLogger().logDebug("Starting ActionBar task!");
            return actionBarTask(player);
        }
    }

    private int actionBarTask(Player player) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, () -> doActionBarTask(player), delay * 20).getTaskId();
    }

    private int repeatingActionBarTask(Player player) {
        UUID uuid = player.getUniqueId();
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            playerMap.put(uuid, new ActionBarData(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if(playerMap.get(uuid).getActionTimer() == duration){
                    plugin.getServer().getScheduler().cancelTask(playerMap.get(uuid).getActionTaskID());
                    playerMap.get(uuid).cancelTask();
                    return;
                }
                doActionBarTask(player);
                playerMap.get(uuid).increaseTimer();
            }, 0, 20).getTaskId()));
        }, delay * 20, repeat * 20).getTaskId();
    }

    public void unloadPlayer(UUID uuid){
        if(playerMap.containsKey(uuid)) {
            playerMap.get(uuid).cancelTask();
            playerMap.remove(uuid);
        }
    }

    public void unloadPlayers(){
        Iterator<Map.Entry<UUID, ActionBarData>> entries = playerMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<UUID, ActionBarData> entry = entries.next();
            entry.getValue().cancelTask();
            entries.remove();
        }
    }

    void doActionBarTask(Player player) {
        NoticeSendEvent event = new NoticeSendEvent(player, plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getUnreadCount(),
                NoticeType.ACTIONBAR);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        ServerAnnouncements.getAudiences().player(player).sendActionBar(
                plugin.getLangHandler().getMessage(player, "noticeHotBar")
                        .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(String.valueOf(event.getUnreadCount())).build()));
    }
}
