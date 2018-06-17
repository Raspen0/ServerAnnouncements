package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.command.CommandSender;

public interface AdminCommand {

    void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin);
}
