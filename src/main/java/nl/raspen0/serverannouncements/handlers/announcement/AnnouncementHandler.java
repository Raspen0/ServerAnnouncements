package nl.raspen0.serverannouncements.handlers.announcement;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AnnouncementHandler {

    private Map<Integer, Announcement> announcements = new HashMap<>();
    private final ServerAnnouncements plugin;

    public AnnouncementHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        loadAnnouncements();
    }

    public Announcement getAnnouncement(int ID){
        return announcements.get(ID);
    }

    public Map<Integer, Announcement> getAnnouncements() {
        return announcements;
    }

    void addAnnounement(int id, Announcement announcement){
        announcements.put(id, announcement);
    }

    void saveAnnouncement(String title, Announcement announcement){
        int ID = announcements.size() + 1;
        announcements.put(ID, announcement);
        FileConfiguration config = getAnnouncementsFile();
        config.set(title + ".text", announcement.getText());
        config.set(title + ".id", ID);
        if(announcement.getPermission() != null){
            config.set(title + ".permission", announcement.getPermission());
        }
        try {
            config.save(new File(plugin.getDataFolder() + File.separator + "announcements.yml"));
        } catch (IOException e) {
            plugin.getPluginLogger().logError("Could not save announcements to file!");
            e.printStackTrace();
        }
    }

    public void reloadAnnouncements(){
        announcements.clear();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::loadAnnouncements);
    }

    FileConfiguration getAnnouncementsFile(){
        File file = new File(plugin.getDataFolder() + File.separator + "announcements.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getPluginLogger().logError("Could not create announcements.yml!");
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public void deleteAnnouncement(String title){
        FileConfiguration file = getAnnouncementsFile();
        int ID = file.getInt(title + ".id");
        file.set(title, null);

        Announcement ann = announcements.get(ID);
        announcements.remove(ID);
        for(Player p : plugin.getServer().getOnlinePlayers()){
            PlayerData data = plugin.getPlayerHandler().getPlayer(p.getUniqueId());
            if(data == null){
                continue;
            }
            if(ann.hasPermission(p)){
                data.decreaseUnreadCount();
            }
            data.removeReadAnnouncement(ID);
            new TaskHandler().reloadPlayer(p, data, plugin);
        }
        plugin.getPlayerHandler().removeReadAnnouncement(ID);
        try {
            file.save(new File(plugin.getDataFolder() + File.separator + "announcements.yml"));
        } catch (IOException e) {
            plugin.getPluginLogger().logError("Could not save announcements.yml!");
            e.printStackTrace();
        }
    }

    private void loadAnnouncements(){
        FileConfiguration config = getAnnouncementsFile();
        for(String s : config.getKeys(false)){
            try {
                String text = config.getString(s + ".text");
                int id = config.getInt(s + ".id");
                String permission = config.getString(s + ".permission");
                if(announcements.put(id, new Announcement(s, text, permission)) != null){
                    plugin.getPluginLogger().logError("The messageID of message" + s + " is conflicting with another message, please check announcements.yml!");
                }
            } catch (NullPointerException e){
                plugin.getPluginLogger().logError("Could not load announcement " + s + "!");
                e.printStackTrace();
            }
        }
    }
}
