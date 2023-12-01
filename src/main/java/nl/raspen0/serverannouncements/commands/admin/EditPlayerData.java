package nl.raspen0.serverannouncements.commands.admin;

import net.kyori.adventure.text.TextReplacementConfig;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditPlayerData implements AdminCommand {

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.editplayer")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        if (args.length < 4) {
            //Not enough args
            MessageUtils.sendLocalisedMessage("notEnoughArgs", sender, plugin);
            return;
        }
        Player player = Bukkit.getPlayer(args[2]);
        if(player == null){
            MessageUtils.sendLocalisedMessage("playerOffline", sender, plugin);
            return;
        }

        if (args.length == 4) {
            if (args[3].equalsIgnoreCase("reset")) {
                plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncements(new ArrayList<>());
                plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), null);

                MessageUtils.sendMessage(plugin.getLangHandler().getMessage(sender, "adminReset")
                        .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(player.getName()).build()), player);
                return;
            } else {
                MessageUtils.sendLocalisedMessage("adminInvalidArg", player, plugin);
                return;
            }
        }
        String title = args[4];
        if(!plugin.getAnnouncementHandler().isAnnouncementLoaded(title)){
            MessageUtils.sendLocalisedMessage("adminInvalidAnnouncement", player, plugin);
            return;
        }
        int id = plugin.getAnnouncementHandler().getAnnouncementID(title);

        if (args[3].equalsIgnoreCase("add")) {
            List<Integer> list = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
            if(list.contains(id)){
                MessageUtils.sendLocalisedMessage("adminAlreadyOnList", player, plugin);
                return;
            }
            list.add(id);
            plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncements(list);
            plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), list);
            MessageUtils.sendMessage(plugin.getLangHandler().getMessage(sender, "adminAdded")
                    .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(player.getName()).build()), player);
        } else if(args[3].equalsIgnoreCase("remove")) {
            List<Integer> list = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
            if(!list.contains(id)){
                MessageUtils.sendLocalisedMessage("adminNotOnList", player, plugin);
                return;
            }
            list.remove(id);
            plugin.getPlayerHandler().getPlayer(player.getUniqueId()).setReadAnnouncements(list);
            plugin.getPlayerHandler().saveReadAnnouncements(player.getUniqueId(), list);
            MessageUtils.sendMessage(plugin.getLangHandler().getMessage(sender, "adminRemoved")
                    .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(player.getName()).build()), player);
        } else {
            MessageUtils.sendLocalisedMessage("adminInvalidArg", player, plugin);
        }
    }
}
