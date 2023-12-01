package nl.raspen0.serverannouncements.commands;

import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.commands.admin.*;
import nl.raspen0.serverannouncements.handlers.announcement.AnnouncementListHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnouncementCommand implements CommandExecutor {

    private final ServerAnnouncements plugin;

    public AnnouncementCommand(ServerAnnouncements plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("announcements")) {
            if (!(sender instanceof Player)) {
                MessageUtils.sendLocalisedMessage("onlyPlayer", sender, plugin);
                return true;
            }

            Player player = (Player) sender;

            String arg = args.length > 0 ? args[0] : "1";
            try {
                int page = Integer.parseInt(arg);
                if (!sender.hasPermission("serverann.view")) {
                    MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
                    return true;
                }
                if (!plugin.getPlayerHandler().hasUnreadAnnouncements(player.getUniqueId())) {
                    MessageUtils.sendLocalisedMessage("announceEmpty", sender, plugin);
                    return true;
                }

                //TODO:
                new AnnouncementListHandler(plugin).sendAnnouncementList(player, page);
                //sendAnnouncementList(page);
                return true;
            } catch (NumberFormatException e){
                if(args[0].equalsIgnoreCase("read")){
                    plugin.getServer().getScheduler().runTask(plugin, () -> markRead(sender));
                    return true;
                }

                if (args[0].equalsIgnoreCase("admin")) {
                    adminCommand(sender, args);
                    return true;
                }
            }
        }
        return true;
    }

    private void adminCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("serverann.admin")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        if (args.length < 2) {
            MessageUtils.sendLocalisedMessage("notEnoughArgs", sender, plugin);
            return;
        }
        try {
            adminCommandList.valueOf(args[1].toUpperCase()).adminCommand.runCommand(sender, args, plugin);
        } catch (IllegalArgumentException e) {
            MessageUtils.sendLocalisedMessage("adminInvalidArg", sender, plugin);
        }
    }

    private void markRead(CommandSender sender) {
        if (!(sender instanceof Player)) {
            MessageUtils.sendLocalisedMessage("onlyPlayer", sender, plugin);
            return;
        }
        if (!sender.hasPermission("serverann.read")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }

        Player player = (Player) sender;
        if (!plugin.getPlayerHandler().hasUnreadAnnouncements(player.getUniqueId())) {
            MessageUtils.sendLocalisedMessage("announceAlreadyRead", sender, plugin);
            return;
        }
        plugin.getPlayerHandler().setReadAnnouncements(player);
        MessageUtils.sendLocalisedMessage("announceClear", sender, plugin);
    }

    @SuppressWarnings("unused")
    private enum adminCommandList {
        CREATE(new AnnouncementCreate()),
        DELETE(new AnnouncementDelete()),
        MODIFY(new AnnouncementModify()),
        LIST(new AnnouncementListCommand()),
        SHOW(new AnnouncementPreview()),
        INFO(new ShowPlayerInfo()),
        RELOAD(new PluginReload()),
        EDITPLAYER(new EditPlayerData());

        AdminCommand adminCommand;

        adminCommandList(AdminCommand adminCommand) {
            this.adminCommand = adminCommand;
        }
    }
}

class AnnouncementDelete implements AdminCommand {
    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.delete")) {
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        if(args.length < 3){
            MessageUtils.sendLocalisedMessage("notEnoughArgs", sender, plugin);
            return;
        }
        if(!plugin.getAnnouncementHandler().isAnnouncementLoaded(args[2])){
            MessageUtils.sendLocalisedMessage("deleteInvalidTitle", sender, plugin);
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getAnnouncementHandler().deleteAnnouncement(args[2]));
        MessageUtils.sendLocalisedMessage("adminDeleted", sender, plugin);
    }
}
