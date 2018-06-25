package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TaskHandler {

    public void startTasks(Player player, PlayerData data, ServerAnnouncements plugin){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getPlayerHandler().addPlayer(player.getUniqueId(), data);
            List<Integer> list = new ArrayList<>();
            if (plugin.isChatEnabled()) {
                if(player.hasPermission("serverann.notification.chat")) {
                    list.add(plugin.getChatHandler().startChatTask(player));
                }
            }
            if (plugin.isActionBarEnabled()) {
                if(player.hasPermission("serverann.notification.actionbar")) {
                    list.add(plugin.getActionBarHandler().startActionBarTask(player));
                }
            }
            if (plugin.isBossBarEnabled()) {
                if(player.hasPermission("serverann.notification.bossbar")) {
                    list.add(plugin.getBossBarHandler().startBossBarTask(player));
                }
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
        if(plugin.getPluginConfig().restartTasksOnUpdate()) {
            data.clearTasks();
            startTasks(player, data, plugin);
        } else {
            plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getPlayerHandler().addPlayer(player.getUniqueId(), data));
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
