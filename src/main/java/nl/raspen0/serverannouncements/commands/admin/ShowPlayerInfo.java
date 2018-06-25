package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class ShowPlayerInfo implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!sender.hasPermission("serverann.admin.info")){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (args.length < 3) {
            //Not enough args
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        PlayerData data;
        Player player = Bukkit.getPlayer(args[2]);
        if (player == null) {
            //TODO: Load data is player is offline.
            sender.sendMessage("Player is not online!");
            return;
        }
        data = plugin.getPlayerHandler().getPlayer(player.getUniqueId());
        StringBuilder builder = new StringBuilder();
        sender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.AQUA + "'s data: ");
        builder.append(ChatColor.AQUA).append("Read Announcements: ");
        if (data.getReadAnnouncements().isEmpty()) {
            builder.append(ChatColor.YELLOW).append("none");
        } else {
            for (Map.Entry e : plugin.getAnnouncementHandler().getLoadedAnnouncements().entrySet()){
                String title = (String) e.getKey();
                int ID = (int) e.getValue();
                if(!data.getReadAnnouncements().contains(ID)){
                    continue;
                }
                if (builder.length() == 2) {
                    builder.append(ChatColor.YELLOW).append(title);
                    continue;
                }
                builder.append(ChatColor.AQUA).append(", ").append(ChatColor.YELLOW).append(title);
            }
        }
        sender.sendMessage(builder.toString());
        sender.sendMessage(ChatColor.AQUA + "Unread Announcements: " + ChatColor.YELLOW + data.getUnreadCount() + ChatColor.AQUA + ".");
    }
}
