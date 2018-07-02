package nl.raspen0.serverannouncements.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.commands.admin.*;
import nl.raspen0.serverannouncements.events.AnnouncementSendEvent;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class AnnouncementCommand implements CommandExecutor {

    private final ServerAnnouncements plugin;

    public AnnouncementCommand(ServerAnnouncements plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("announcements")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "onlyPlayer"));
                return true;
            }
            if (!sender.hasPermission("serverann.view")) {
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
                return true;
            }
            Player player = (Player) sender;
            if(!plugin.getPlayerHandler().isPlayerLoaded(player.getUniqueId())){
                if(args.length > 0) {
                    if (args[0].equalsIgnoreCase("read")) {
                        sender.sendMessage(plugin.getLangHandler().getMessage(sender, "announceAlreadyRead"));
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("admin")) {
                        adminCommand(sender, args);
                        return true;
                    }
                }
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceEmpty"));
                return true;
            }
            List<Integer> read = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                int startPoint = 1;
                int page = 1;
                if (args.length >= 1) {
                    try {
                        page = Integer.parseInt(args[0]);
                        if (page > 1) {
                            startPoint = ((page - 1) * 3) + 1;
                        }
                    } catch (NumberFormatException e) {
                        if (args[0].equalsIgnoreCase("read")) {
                            plugin.getServer().getScheduler().runTask(plugin, () -> markRead(sender));
                            return;
                        }
                        if (args[0].equalsIgnoreCase("admin")) {
                            adminCommand(sender, args);
                            return;
                        }
                        //Default to first page.
                    }
                }
                final Map<Integer, Announcement> map = plugin.getAnnouncementHandler().getAnnouncements();

                //If its the first line/
                boolean empty = true;

                int announcementCount = 1;
                final int pageSize = startPoint + plugin.getPluginConfig().getAnnouncementsPerPage();

                //Loop through Announcement map.
                for (int ID : map.keySet()) {
                    if (read.contains(ID)) {
                        //If the player has already read this Announcement.
                        continue;
                    }
                    if (!map.get(ID).hasPermission(player)) {
                        //If the player does not have permission for this Announcement.
                        continue;
                    }
                    if (empty) {
                        //If it's the first line, send the header.
                        plugin.getServer().getScheduler().runTask(plugin, () ->
                                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceHeader")));
                        empty = false;
                    }

                    if (announcementCount < startPoint) {
                        //Skip to start number.
                        //Not first check because the read and permission checks also skip Announcements outside the announcementCount.
                        announcementCount++;
                        continue;
                    }

                    if (announcementCount == pageSize) {
                        //Send the next page message.
                        final String nextPage = String.valueOf(page + 1);
                        if(plugin.isSpigot()){
                            plugin.getServer().getScheduler().runTask(plugin, () -> {
                                String[] message = plugin.getLangHandler().getMessage(sender, "announceNextPage").split("\\s.0.\\s");
                                TextComponent textComponent = new TextComponent(message[0]);
                                textComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);

                                TextComponent clickComponent = new TextComponent(" /ann " + nextPage + " ");
                                clickComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
                                clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ann " + nextPage));
                                textComponent.addExtra(clickComponent);

                                TextComponent textComponent3 = new TextComponent(message[1]);
                                textComponent3.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                                textComponent.addExtra(textComponent3);

                                player.spigot().sendMessage(textComponent);
                            });
                        } else {
                            plugin.getServer().getScheduler().runTask(plugin, () ->
                                    player.sendMessage(plugin.getLangHandler().getMessage(player, "announceNextPage")
                                            .replace("{0}", "/ann " + nextPage)));
                        }
                        return;
                    }

                    //Send Announcement
                    plugin.getServer().getScheduler().runTask(plugin, () ->
                            sendMessage(player, map.get(ID).getDate(plugin.getLangHandler().getMessage(player, "locale")), "- " + map.get(ID).getText()));

                    //Increase Announcement count.
                    announcementCount++;
                }

                //Send empty or footer messages depending on if there where any announcements.
                boolean finalEmpty = empty;
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (finalEmpty) {
                        player.sendMessage(plugin.getLangHandler().getMessage(player, "announceEmpty"));
                    } else {
                        player.sendMessage(plugin.getLangHandler().getMessage(player, "announceFooter"));
                    }
                });
            });
        }
        return true;
    }

    private void adminCommand(CommandSender sender, String[] args){
        if (!sender.hasPermission("serverann.admin")) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (args.length < 2) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        try {
            adminCommandList.valueOf(args[1].toUpperCase()).adminCommand.runCommand(sender, args, plugin);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidArg"));
        }
    }

    private void markRead(CommandSender sender){
        if (!sender.hasPermission("serverann.read")) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "onlyPlayer"));
            return;
        }
        Player player = (Player) sender;
        plugin.getPlayerHandler().setReadAnnouncements(player);
        player.sendMessage(plugin.getLangHandler().getMessage(player, "announceClear"));
    }

    @SuppressWarnings("unused")
    private enum adminCommandList {
        CREATE(new AnnouncementCreate()),
        DELETE(new AnnouncementDelete()),
        MODIFY(new AnnouncementModify()),
        LIST(new AnnouncementList()),
        SHOW(new AnnouncementPreview()),
        INFO(new ShowPlayerInfo()),
        RELOAD(new PluginReload()),
        EDITPLAYER(new EditPlayerData());

        AdminCommand adminCommand;

        adminCommandList(AdminCommand adminCommand) {
            this.adminCommand = adminCommand;
        }
    }

    private void sendMessage(Player player, String date, String message) {
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        AnnouncementSendEvent event = new AnnouncementSendEvent(player, message, date);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            if (plugin.getPluginConfig().showDate()) {
                player.sendMessage(ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET + message);
            } else {
                player.sendMessage(message);
            }
        }
    }
}

class AnnouncementDelete implements AdminCommand {
    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if (!sender.hasPermission("serverann.admin.delete")) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getAnnouncementHandler().deleteAnnouncement(args[2]));
        sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminDeleted"));
    }
}
