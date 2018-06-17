package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class AnnouncementList implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry e : plugin.getAnnouncementHandler().getAnnouncements().entrySet()) {
            Announcement ann = (Announcement) e.getValue();
            if (builder.length() == 0) {
                builder.append(ChatColor.AQUA).append(ann.getTitle());
                continue;
            }
            builder.append(ChatColor.YELLOW).append(", ").append(ChatColor.AQUA).append(ann.getTitle());
        }
        sender.sendMessage("Loaded Announcements: ");
        sender.sendMessage(builder.toString());
    }
}
