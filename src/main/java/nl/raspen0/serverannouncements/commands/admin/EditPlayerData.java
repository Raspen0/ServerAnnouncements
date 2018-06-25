package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditPlayerData implements AdminCommand {

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.editplayer")) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (args.length < 4) {
            //Not enough args
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        Player player = Bukkit.getPlayer(args[2]);
        if(player == null){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "playerOffline"));
            return;
        }

        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("reset")) {
                plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncement(new ArrayList<>());
                plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), null);
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminReset").replace("{0}", player.getName()));
                return;
            } else {
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidArg"));
                return;
            }
        }
        String title = args[4];
        if(!plugin.getAnnouncementHandler().isAnnouncementLoaded(title)){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidAnnouncement"));
            return;
        }
        int id = plugin.getAnnouncementHandler().getAnnouncementID(title);

        if (args[3].equalsIgnoreCase("add")) {
            List<Integer> list = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
            if(list.contains(id)){
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminAlreadyOnList"));
                return;
            }
            list.add(id);
            plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncement(list);
            plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), list);
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminAdded").replace("{0}", player.getName()));
        } else if(args[3].equalsIgnoreCase("remove")) {
            List<Integer> list = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
            if(!list.contains(id)){
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminNotOnList"));
                return;
            }
            list.remove(id);
            plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncement(list);
            plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), list);
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminRemoved").replace("{0}", player.getName()));
        } else {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidArg"));
        }
    }
}
