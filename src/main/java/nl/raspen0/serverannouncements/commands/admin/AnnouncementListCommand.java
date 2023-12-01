package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class AnnouncementListCommand implements AdminCommand {

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.list")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry e : plugin.getAnnouncementHandler().getLoadedAnnouncements().entrySet()) {
                String title = (String) e.getKey();
                int id = (int) e.getValue();

                if (builder.length() == 0) {
                    builder.append(ChatColor.AQUA).append(title).append("(").append(ChatColor.YELLOW).append(id).append(ChatColor.AQUA).append(")");
                    continue;
                }
                builder.append(", ").append(ChatColor.AQUA).append(title).append("(").append(ChatColor.YELLOW)
                        .append(id).append(ChatColor.AQUA).append(")");
            }
            if(builder.length() == 0){
                MessageUtils.sendLocalisedMessage("adminListEmpty", sender, plugin);
                return;
            }

            MessageUtils.sendLocalisedMessage("adminList", sender, plugin);
            sender.sendMessage(builder.toString());
        });
    }
}
