package nl.raspen0.serverannouncements.handlers.announcement;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageCreator implements Listener {

    private final ServerAnnouncements plugin;
    private Map<UUID, Announcement.AnnouncementBuilder> playerMap;

    public enum creatorState {
        TITLE, TEXT, PERMISSION
    }

    public MessageCreator(ServerAnnouncements plugin){
        this.plugin = plugin;
        playerMap = new HashMap<>();
    }

    public void addPlayer(UUID uuid){
        playerMap.put(uuid, new Announcement.AnnouncementBuilder());
    }

    public void removePlayer(UUID uuid){
        playerMap.remove(uuid);
    }

    public void removePlayers(){
        playerMap.clear();
    }

    @EventHandler
    public void chatListener(AsyncPlayerChatEvent event){
        if(!playerMap.containsKey(event.getPlayer().getUniqueId())){
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if(event.getMessage().equals("cancel")){
            player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorCancelled"));
            playerMap.remove(player.getUniqueId());
        }
        switch (playerMap.get(player.getUniqueId()).getState()){
            case TITLE: {
                if(plugin.getAnnouncementHandler().isAnnouncementLoaded(event.getMessage())){
                    player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorTitleExists"));
                    return;
                }
                playerMap.get(player.getUniqueId()).setTitle(event.getMessage());
                player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorText"));
                break;
            }
            case TEXT: {
                playerMap.get(player.getUniqueId()).setText(event.getMessage());
                player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorPermission"));
                break;
            }
            case PERMISSION: {
                Announcement.AnnouncementBuilder ann = playerMap.get(player.getUniqueId());
                String permission = event.getMessage();
                if(!permission.contains(".") && !permission.equals("none")){
                    player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorInvalidPermission"));
                    return;
                }
                if(!permission.equalsIgnoreCase("none")){
                    ann.setPermission(event.getMessage());
                }
                String rawText = ann.getText();
                ann.setText(ChatColor.translateAlternateColorCodes('&', rawText));
                Announcement announcement = new Announcement(ann);
                plugin.getAnnouncementHandler().saveNewAnnouncement(ann.getTitle(), announcement, rawText);
                playerMap.remove(player.getUniqueId());

                for(Player p : plugin.getServer().getOnlinePlayers()){
                    PlayerData data = plugin.getPlayerHandler().getPlayer(p.getUniqueId());
                    if(data == null){
                        //Load data of online player if it wasn't loaded before
                        plugin.getPluginLogger().logDebug("Data of " + p.getName() + " was not loaded, loading from file.");
                        data = plugin.getPlayerHandler().loadPlayer(p);
                        if(data.getUnreadCount() == 0){
                            continue;
                        }
                    }
                    if(announcement.hasPermission(p)){
                        data.increaseUnreadCount();
                        new TaskHandler().reloadPlayer(p, data, plugin);
                    }
                }
                player.sendMessage(plugin.getLangHandler().getMessage(player, "creatorFinish"));
            }
        }
    }
}



