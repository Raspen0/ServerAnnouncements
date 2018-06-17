package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEventHandler implements Listener {

    private final ServerAnnouncements plugin;

    public PlayerEventHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            PlayerData data = plugin.getPlayerHandler().loadPlayer(e.getPlayer());
            if (data.getUnreadCount() > 0) {
                new TaskHandler().startTasks(e.getPlayer(), data, plugin);
            }
        });
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        plugin.getAnnouncementCreator().removePlayer(event.getPlayer().getUniqueId());
        new TaskHandler().unloadPlayer(event.getPlayer(), plugin);
    }
}
