package nl.raspen0.serverannouncements.handlers.announcement;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import nl.raspen0.serverannouncements.AnnouncementList;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.SpigotAnnouncementList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class AnnouncementListHandler {

    private final ServerAnnouncements plugin;

    public AnnouncementListHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public void sendAnnouncementList(Player player, int page){
        //Contains Announcement ID's.
        List<Integer> read = plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getReadAnnouncements();

        //Get all Announcements.
        //(ID, Announcement)
        Map<Integer, Announcement> map = plugin.getAnnouncementHandler().getAnnouncements();

        if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
            System.out.println("Using PlaceholderAPI");
            for(Map.Entry<Integer, Announcement> e : map.entrySet()){
                if(read.contains(e.getKey()) && !e.getValue().hasPermission(player)){
                    continue;
                }
                System.out.println("Placeholder " + e.getKey());
                Announcement ann = e.getValue();
                ann.updateText(PlaceholderAPI.setPlaceholders(player, ann.getText()));
                e.setValue(ann);
            }
        }

        //Async
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            final int pageSize = plugin.getPluginConfig().getAnnouncementsPerPage();

            int start = 1;


            int toSkip = 1;
            if (page > 1) {
                toSkip = ((page - 1) * pageSize);
            }

            System.out.println("To skip: " + toSkip);

            //Index to stop the loop at.
            //final int maxAnnCount = pageSize + 1;
            AnnouncementList annList;
            //   if (plugin.isSpigot()) {
            annList = new SpigotAnnouncementList(pageSize);
            //    } else {
            //        annList = new BukkitAnnouncementList(pageSize);
            //    }


            int mapPos = start;
            //The current number of announcements.
            int annCount = 0;


            for(Map.Entry<Integer, Announcement> e : map.entrySet()){
                int annID = e.getKey();
                Announcement ann = e.getValue();

                plugin.getPluginLogger().logDebug("Processing ID: " + annID + " - " + ann.getTitle() + ", annCount: " + annCount);

                if(read.contains(annID)){
                    plugin.getPluginLogger().logDebug("ID: " + annID + " did not pass read check!");
                    mapPos++;
                    continue;
                }

                if(!ann.hasPermission(player)){
                    plugin.getPluginLogger().logDebug("ID: " + annID + " did not pass permission check!");
                    mapPos++;
                    continue;
                }

                if (toSkip > 0) {
                    //Skip to the correct announcement.
                    mapPos++;
                    toSkip--;
                    continue;
                }

                plugin.getPluginLogger().logDebug("ID: " + annID + " passed check!");

                String date = ann.getDate(plugin.getLangHandler().getMessage(player, "locale"));
                String message = "- " + ann.getText();

                TextComponent textComponent;

                if (message.contains("url:")) {
                    System.out.println("URL message");
                    textComponent = createUrlMessage(date, message);
                    //addAnnouncement(textComponent, annCount, mapPos, page, map.size(), annList, player);
                } else {
                    System.out.println("Normal message");
                    textComponent = new TextComponent((plugin.getPluginConfig().showDate() ?
                            (ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET) : "") + message);
                }

                System.out.println("Adding announcement: " + annID);
                if(annList.addAnnouncement(textComponent, annCount)){
                    System.out.println("Full list count" + (annCount + 1) + ".");
                    break;
                }

                mapPos++;
                annCount++;
            }

            if(annCount == 0) {
                System.out.println("Page to high 2");
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announcePageTooHigh"));
                return;
            }

            if(mapPos < map.size()){
                annList.setNextPage();
            }

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceHeader"));
                annList.sendAnnouncements(player);
                if (annList.hasNextPage()) {
                    final String nextPage = String.valueOf(page + 1);
                  //  if (plugin.isSpigot()) {
                    player.spigot().sendMessage(sendNextPageMessage(player, nextPage));
                 //   } else {
                 //       player.sendMessage(plugin.getLangHandler().getMessage(player, "announceNextPage")
                 //               .replace("{0}", "/ann " + nextPage));
                 //   }
                }
                player.sendMessage(plugin.getLangHandler().getMessage(player, "announceFooter"));
            });
        });
    }

    private TextComponent createUrlMessage(String date, String message) {
//        if (!plugin.isSpigot()) {
//            plugin.getServer().getScheduler().runTask(plugin, () ->
//                    plugin.getPluginLogger().logError("Announcements with urls are not supported with CraftBukkit, these announcements will be skipped!"));
//            return null;
//        }
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
                BaseComponent[] text = TextComponent.fromLegacyText(s);
                for(BaseComponent b : text){
                    textComponent.addExtra(b);
                }
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
}
