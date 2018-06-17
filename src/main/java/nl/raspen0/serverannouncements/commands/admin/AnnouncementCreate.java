package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnouncementCreate implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only player!");
            return;
        }
        Player player = (Player) sender;
        plugin.getAnnouncementCreator().addPlayer(player.getUniqueId());
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorHeader"));
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorStart"));
        player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorTitle"));
    }
}
