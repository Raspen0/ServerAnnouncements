package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.Announcement;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

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

    Announcement getAnnouncement(int ID){
        return announcements.get(ID);
    }

    public Map<Integer, Announcement> getAnnouncements() {
        return announcements;
    }

    public void reloadAnnouncements(){
        announcements.clear();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::loadAnnouncements);
    }

    private void loadAnnouncements(){
        File file = new File(plugin.getDataFolder() + File.separator + "announcements.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for(String s : config.getKeys(false)){
            try {
                String text = config.getString(s + ".text");
                int id = config.getInt(s + ".id");
                String permission = config.getString(s + ".permission");
                if(announcements.put(id, new Announcement(text, permission)) != null){
                    plugin.getLogger().info("The messageID of message" + s + " is conflicting with another message, please check announcements.yml!");
                }
            } catch (NullPointerException e){
                System.out.println("Failed to load message " + s + "!");
            }
        }
    }
}
