package nl.raspen0.serverannouncements.handlers.announcement;

import nl.raspen0.serverannouncements.*;
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

      /*  if(plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
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

       */

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
            AnnouncementList annList = new AnnouncementList(pageSize);

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

                //TODO: Move locale outside of message list.
                String date = ann.getDate(String.valueOf(plugin.getLangHandler().getMessage(player, "locale")));

                System.out.println("Adding announcement: " + annID);
                annList.addAnnouncement(ann.getText(), annCount, plugin, player, date);
                if(annCount == pageSize){
                    System.out.println("Full list count" + (annCount + 1) + ".");
                    break;
                }

                mapPos++;
                annCount++;
            }

            if(annCount == 0) {
                System.out.println("Page to high 2");
                MessageUtils.sendLocalisedMessage("announcePageTooHigh", player, plugin);
                return;
            }

            if(mapPos < map.size()){
                annList.setNextPage();
            }

            //Send the list on the main thread.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                MessageUtils.sendLocalisedMessage("announceHeader", player, plugin);
                annList.sendAnnouncements(player);
                if (annList.hasNextPage()) {
                    MessageUtils.sendNextPageMessage(page + 1, player, plugin);
                }
                MessageUtils.sendLocalisedMessage("announceFooter", player, plugin);
            });
        });
    }
}
