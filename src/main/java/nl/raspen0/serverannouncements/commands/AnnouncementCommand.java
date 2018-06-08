package nl.raspen0.serverannouncements.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import nl.raspen0.serverannouncements.Announcement;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.AnnouncementSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class AnnouncementCommand implements CommandExecutor {

    private final ServerAnnouncements plugin;

    public AnnouncementCommand(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("announcements")) {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    return true;
                }
                Player player = (Player) sender;
                List<Integer> read = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
                if(read == null){
                    return true;
                }
                Map<Integer, Announcement> map = plugin.getAnnouncementHandler().getAnnouncements();
                int count = 0;
                for (int ID : map.keySet()) {
                    if (read.contains(ID)) {
                        continue;
                    }
                    if(!map.get(ID).hasPermission(player)){
                        continue;
                    }
                    if(count == 0){
                        player.sendMessage(plugin.getLangHandler().getMessage("announceHeader"));
                    }
                    count++;
                    sendMessage(player, "- " + map.get(ID).getText());
                }
                if(count == 0){
                    player.sendMessage(plugin.getLangHandler().getMessage("announceEmpty"));
                } else {
                    player.sendMessage(plugin.getLangHandler().getMessage("announceFooter"));
                }
                return true;
            } else {
                if(args[0].equalsIgnoreCase("read")){
                    if (!(sender instanceof Player)) {
                        return true;
                    }
                    Player player = (Player) sender;
                    plugin.getPlayerHandler().setReadAnnouncements(player);
                    player.sendMessage(plugin.getLangHandler().getMessage("announceClear"));
                    return true;
                }

                if(args[0].equalsIgnoreCase("reload")){
                    plugin.reloadData(sender);
                }
            }
        }
        return true;
    }

    private void sendMessage(Player player, String message){
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        AnnouncementSendEvent event = new AnnouncementSendEvent(player, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            player.sendMessage(message);
        }
    }
}
