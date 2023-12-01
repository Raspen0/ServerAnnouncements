package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.command.CommandSender;

public class AnnouncementPreview implements AdminCommand {

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.preview")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        if (args.length < 3) {
            MessageUtils.sendLocalisedMessage("notEnoughArgs", sender, plugin);
            return;
        }
        String title = args[2];
        Announcement ann = plugin.getAnnouncementHandler().getAnnouncement(title);
        if (ann == null) {
            MessageUtils.sendLocalisedMessage("adminInvalidAnnouncement", sender, plugin);
            return;
        }
        sender.sendMessage("- " + ann.getText());
    }
}
