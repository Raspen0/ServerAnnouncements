package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.AnnouncementModifier;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class AnnouncementModify implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (args.length < 4) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        String change = args[2].toLowerCase();
        if (!change.equals("text") && !change.equals("permission") && !change.equals("title")) {
            ChatColor YELLOW = ChatColor.YELLOW;
            ChatColor RED = ChatColor.RED;
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminValidArgs") + YELLOW + "text" + RED +
                    ", " + YELLOW + "title" + RED + ", " + "permission" + RED + ".");
            return;
        }
        String title = args[3];
        new AnnouncementModifier(plugin).modifyAnnouncement(sender, title, change, args[4]);
    }
}
