package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnouncementCreate implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!(sender instanceof Player)){
            MessageUtils.sendLocalisedMessage("OnlyPlayer", sender, plugin);
            return;
        }
        if(!sender.hasPermission("serverann.admin.creator")){
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        Player player = (Player) sender;
        plugin.getAnnouncementCreator().addPlayer(player.getUniqueId());
        MessageUtils.sendLocalisedMessage("creatorHeader", sender, plugin);
        MessageUtils.sendLocalisedMessage("creatorStart", sender, plugin);
        MessageUtils.sendLocalisedMessage("creatorTitle", sender, plugin);
    }
}
