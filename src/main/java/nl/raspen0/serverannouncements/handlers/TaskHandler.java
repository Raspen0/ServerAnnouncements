package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskHandler {

    public void startTasks(Player player, PlayerData data, ServerAnnouncements plugin){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getPlayerHandler().addPlayer(player.getUniqueId(), data);
            List<Integer> list = new ArrayList<>();
            if (plugin.isChatEnabled()) {
                list.add(plugin.getChatHandler().startChatTask(player));
            }
            if (plugin.isActionBarEnabled()) {
                list.add(plugin.getActionBarHandler().startActionBarTask(player));
            }
            if (plugin.isBossBarEnabled()) {
                list.add(plugin.getBossBarHandler().startBossBarTask(player));
            }
            if(!list.isEmpty()) {
                plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setTasks(list);
            }
        });
    }

    public void reloadPlayer(Player player, PlayerData data, ServerAnnouncements plugin){
        if(data.getUnreadCount() == 0){
            unloadPlayer(player, plugin);
            return;
        }
        if(plugin.shouldRestartTasksOnUpdate()) {
            data.clearTasks();
            startTasks(player, data, plugin);
        } else {
            plugin.getPlayerHandler().addPlayer(player.getUniqueId(), data);
        }
    }

    void unloadPlayer(Player player, ServerAnnouncements plugin){
        plugin.getPlayerHandler().unloadPlayer(player.getUniqueId());
        if(plugin.isBossBarEnabled()){
            plugin.getBossBarHandler().unloadPlayer(player);
        }
        if(plugin.isActionBarEnabled()){
            plugin.getActionBarHandler().unloadPlayer(player.getUniqueId());
        }
    }
}
