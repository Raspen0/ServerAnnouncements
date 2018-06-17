package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.command.CommandSender;

public class AnnouncementPreview implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (args.length < 3) {
            //Not enough args
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        try {
            int id = Integer.parseInt(args[2]);
            Announcement ann = plugin.getAnnouncementHandler().getAnnouncement(id);
            if (ann == null) {
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidAnnouncement"));
                return;
            }
            sender.sendMessage("- " + ann.getText());
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminShowNoID"));
        }
    }
}
