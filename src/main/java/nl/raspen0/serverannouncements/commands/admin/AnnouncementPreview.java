package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.command.CommandSender;

public class AnnouncementPreview implements AdminCommand {

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.preview")) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        String title = args[2];
        Announcement ann = plugin.getAnnouncementHandler().getAnnouncement(title);
        if (ann == null) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidAnnouncement"));
            return;
        }
        sender.sendMessage("- " + ann.getText());
    }
}
