package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnouncementCreate implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!(sender instanceof Player)){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "OnlyPlayer"));
            return;
        }
        if(!sender.hasPermission("serverann.admin.creator")){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        Player player = (Player) sender;
        plugin.getAnnouncementCreator().addPlayer(player.getUniqueId());
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorHeader"));
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorStart"));
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorTitle"));
    }
}
