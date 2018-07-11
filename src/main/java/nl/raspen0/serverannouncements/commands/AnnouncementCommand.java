package nl.raspen0.serverannouncements.commands;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.raspen0.serverannouncements.AnnouncementList;
import nl.raspen0.serverannouncements.BukkitAnnouncementList;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.SpigotAnnouncementList;
import nl.raspen0.serverannouncements.commands.admin.*;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
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

            //If player has already read all Announcements.
            if (!plugin.getPlayerHandler().isPlayerLoaded(player.getUniqueId())) {
                if (args.length > 0) {
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
                final int pageSize = plugin.getPluginConfig().getAnnouncementsPerPage();
                int toSkip = 1;
                int page = 1;
                if (args.length >= 1) {
                    try {
                        page = Integer.parseInt(args[0]);
                        if (page > 1) {
                            toSkip = ((page - 1) * pageSize);
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
                final int maxAnnCount = pageSize + 1;
                AnnouncementList annList;
                if (plugin.isSpigot()) {
                    annList = new SpigotAnnouncementList(pageSize);
                } else {
                    annList = new BukkitAnnouncementList(pageSize);
                }
                int mapPos = 1;
                int annCount = 1;
                for(int ID : map.keySet()){
                    plugin.getPluginLogger().logDebug("Processing ID: " + ID + ", annCount: " + annCount);
                    if(!annCheck(read, ID, map, player)){
                        plugin.getPluginLogger().logDebug("ID: " + ID + " did not pass check!");
                        if(mapPos == map.size()){
                            //If thee annList is not full
                            //4 because this is after 3 Announcements should have been added.
                            if(annCount < maxAnnCount){
                                final int finalAnnCount = annCount - 1;
                                if(finalAnnCount == 0){
                                    plugin.getServer().getScheduler().runTask(plugin, () -> player.sendMessage(plugin.getLangHandler().getMessage(player, "announcePageTooHigh")));
                                    return;
                                }
                                plugin.getPluginLogger().logDebug("The amount of Announcements is less then the pageSize, changing pageSize to " + finalAnnCount);
                                plugin.getServer().getScheduler().runTask(plugin, () -> annList.setTotal(finalAnnCount));
                            }
                            return;
                        }
                        mapPos++;
                        continue;
                    }
                    plugin.getPluginLogger().logDebug("ID: " + ID + " passed check!");
                    if(annCount == maxAnnCount){
                        plugin.getPluginLogger().logDebug("annCount is higher then pageSize, nextPage message will be send.");
                        plugin.getServer().getScheduler().runTask(plugin, annList::setNextPage);
                        return;
                    }

                    if (annCount == 1) {
                        if(page > 1){
                            if(toSkip > 0){
                                mapPos++;
                                toSkip--;
                                continue;
                            }
                        }
                    }

                    final String date = map.get(ID).getDate(plugin.getLangHandler().getMessage(player, "locale"));
                    final int finalMapPos = mapPos;
                    final int finalAnnCount = annCount;
                    final int finalPage = page;

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        String message = "- " + map.get(ID).getText();
                        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                            message = PlaceholderAPI.setPlaceholders(player, message);
                        }
                        final String finalMessage = message;
                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                            if (finalMessage.contains("url:")) {
                                if (!plugin.isSpigot()) {
                                    plugin.getServer().getScheduler().runTask(plugin, () ->
                                            plugin.getPluginLogger().logError("Announcements with urls are not supported with CraftBukkit, these announcements will be skipped!"));
                                    return;
                                }
                                TextComponent textComponent = createUrlMessage(date, finalMessage);
                                addAnnouncement(textComponent, finalAnnCount, finalMapPos, finalPage, map.size(), annList, player);
                            } else {
                                if (plugin.getPluginConfig().showDate()) {
                                    addAnnouncement(ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET + finalMessage,
                                            finalAnnCount, finalMapPos, finalPage, map.size(), annList, player);
                                } else {
                                    addAnnouncement(finalMessage, finalAnnCount, finalMapPos, finalPage, map.size(), annList, player);
                                }
                            }
                        });
                    });
                    annCount++;
                    mapPos++;
                }
                if(annCount == 1) {
                    System.out.println("Done with loop!");
                    plugin.getServer().getScheduler().runTask(plugin, () -> player.sendMessage(plugin.getLangHandler().getMessage(player, "announcePageTooHigh")));
                }
            });
        }
        return true;
    }

    private void addAnnouncement(Object announcement, int annCount, int mapPos, int page, int mapSize, AnnouncementList annList, Player player) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (annList.addAnnouncement(announcement, annCount) || mapPos == mapSize) {
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceHeader"));
                annList.sendAnnouncements(player);
                if (annList.hasNextPage()) {
                    final String nextPage = String.valueOf(page + 1);
                    if (plugin.isSpigot()) {
                        player.spigot().sendMessage(sendNextPageMessage(player, nextPage));
                    } else {
                        player.sendMessage(plugin.getLangHandler().getMessage(player, "announceNextPage")
                                .replace("{0}", "/ann " + nextPage));
                    }
                }
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceFooter"));
            }
        });
    }

    private boolean annCheck(List<Integer> read, int ID, Map<Integer, Announcement> map, Player player) {
        return !read.contains(ID) && map.get(ID).hasPermission(player);
    }


    private TextComponent createUrlMessage(String date, String message) {
        if (!plugin.isSpigot()) {
            plugin.getServer().getScheduler().runTask(plugin, () ->
                    plugin.getPluginLogger().logError("Announcements with urls are not supported with CraftBukkit, these announcements will be skipped!"));
            return null;
        }
        //This is a url:(https://google.com, link).
        String[] message2 = message.split("url:");
        TextComponent textComponent = new TextComponent();
        if (plugin.getPluginConfig().showDate()) {
            textComponent.addExtra(ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET);
        }
        for (String s : message2) {
            if (s.contains("http") || s.contains("https")) {
                String formattedString = s.replace("(", "");
                String[] link = formattedString.split(", ");
                String[] linkMessage = link[1].split("\\)");

                TextComponent linkComponent = new TextComponent(linkMessage[0]);
                linkComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, link[0]));

                if (plugin.getPluginConfig().getHoverMessage().equals("url")) {
                    linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(link[0])));
                } else if (plugin.getPluginConfig().getHoverMessage().equals("message")) {
                    linkComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("Go to website.")));
                }
                textComponent.addExtra(linkComponent);
                textComponent.addExtra(linkMessage[1]);
            } else {
                textComponent.addExtra(s);
            }
        }
        return textComponent;
    }

    private TextComponent sendNextPageMessage(Player player, String nextPage) {
        String[] message = plugin.getLangHandler().getMessage(player, "announceNextPage").split("\\s.0.\\s");
        TextComponent textComponent = new TextComponent(message[0]);
        textComponent.setColor(net.md_5.bungee.api.ChatColor.AQUA);

        TextComponent clickComponent = new TextComponent(" /ann " + nextPage + " ");
        clickComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        clickComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ann " + nextPage));
        textComponent.addExtra(clickComponent);

        TextComponent textComponent3 = new TextComponent(message[1]);
        textComponent3.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        textComponent.addExtra(textComponent3);
        return textComponent;
    }

    private void adminCommand(CommandSender sender, String[] args) {
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

    private void markRead(CommandSender sender) {
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
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> plugin.getAnnouncementHandler().deleteAnnouncement(args[2]));
        sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminDeleted"));
    }
}
