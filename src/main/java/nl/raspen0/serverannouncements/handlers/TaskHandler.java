package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

public class TaskHandler {

    private final ServerAnnouncements plugin;

    public TaskHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public void startTasks(Player player, PlayerData data, int count){
        if(plugin.isChatEnabled()) {
            data.addTask(plugin.getChatHandler().startChatTask(player, String.valueOf(count)));
        }
        if(plugin.isActionBarEnabled()) {
            data.addTask(plugin.getActionBarHandler().startActionBarTask(player, String.valueOf(count)));
        }
        if(plugin.isBossBarEnabled()) {
           data.addTask(plugin.getBossBarHandler().startBossBarTask(player, String.valueOf(count)));
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getPlayerHandler().addPlayer(player.getUniqueId(), data));
    }


}
