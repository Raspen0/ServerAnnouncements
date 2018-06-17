package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShowPlayerInfo implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
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
            for (int i : data.getReadAnnouncements()) {
                System.out.println(i);
                if (builder.length() == 2) {
                    builder.append(ChatColor.YELLOW).append(i);
                    continue;
                }
                builder.append(ChatColor.AQUA).append(", ").append(ChatColor.YELLOW).append(i);
            }
        }
        sender.sendMessage(builder.toString());
        sender.sendMessage(ChatColor.AQUA + "Unread Announcements: " + ChatColor.YELLOW + data.getUnreadCount() + ChatColor.AQUA + ".");
    }
}
