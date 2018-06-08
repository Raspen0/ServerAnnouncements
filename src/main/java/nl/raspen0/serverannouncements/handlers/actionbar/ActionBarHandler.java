package nl.raspen0.serverannouncements.handlers.actionbar;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class ActionBarHandler {

    private final ServerAnnouncements plugin;
    private int delay;
    private int repeat;
    private int duration;

    ActionBarHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        delay = plugin.getConfig().getInt("notification.actionbar.delay");
        repeat = plugin.getConfig().getInt("notification.actionbar.repeat");
        duration = plugin.getConfig().getInt("notification.actionbar.duration") - 2;
    }

    public int startActionBarTask(Player player, String count){
        if(repeat > 0){
            return repeatingActionBarTask(player, count);
        } else {
            return actionBarTask(player, count);
        }
    }

    private int actionBarTask(Player player, String count) {
        return plugin.getServer().getScheduler().runTaskLater(plugin, () -> doActionBarTask(player, count), delay * 20).getTaskId();
    }

    private int repeatingActionBarTask(Player player, String count) {
        UUID uuid = player.getUniqueId();
        return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            plugin.getPlayerHandler().getPlayer(uuid).setActionbarTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if(plugin.getPlayerHandler().getPlayer(uuid).getActionbarTimer() == duration){
                    plugin.getServer().getScheduler().cancelTask(plugin.getPlayerHandler().getPlayer(uuid).getActionbarTask());
                    plugin.getPlayerHandler().getPlayer(uuid).clearActionbarTimer();
                    return;
                }
                doActionBarTask(player, count);
                plugin.getPlayerHandler().getPlayer(player.getUniqueId()).increaseActionbarTimer();
            }, 0, 20).getTaskId());
        }, delay * 20, repeat * 20).getTaskId();
    }

    abstract void doActionBarTask(Player player, String count);
}
