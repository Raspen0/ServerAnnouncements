package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PluginReload implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!sender.hasPermission("serverann.admin.reload")){
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        plugin.loadConfig();
        plugin.getLangHandler().reloadMessages();
        plugin.getAnnouncementHandler().reloadAnnouncements();
        plugin.getPlayerHandler().unloadPlayers();
        if(plugin.isBossBarEnabled()){
            plugin.getBossBarHandler().unloadPlayers();
        }
        if(plugin.isActionBarEnabled()){
            plugin.getActionBarHandler().unloadPlayers();
        }
        for(Player player : plugin.getServer().getOnlinePlayers()){
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                PlayerData data = plugin.getPlayerHandler().loadPlayer(player);
                new TaskHandler().startTasks(player, data, plugin);
            });
        }
        MessageUtils.sendLocalisedMessage("announceReloaded", sender, plugin);
    }
}
