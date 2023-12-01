package nl.raspen0.serverannouncements.handlers.announcement;

import net.kyori.adventure.text.Component;
import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.MessageUtils;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class AnnouncementHandler {

    //ID, Announcement
    //LinkedHashMap for keeping the correct order.
    private final Map<Integer, Announcement> announcements = new LinkedHashMap<>();

    //Title, ID
    @Deprecated
    private final Map<String, Integer> loadedAnnouncements = new HashMap<>();
    private final ServerAnnouncements plugin;

    public AnnouncementHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        loadAnnouncements();
    }

    public Announcement getAnnouncement(int ID){
        return announcements.get(ID);
    }

    @Deprecated
    public Announcement getAnnouncement(String title){
        for(Announcement ann : announcements.values()){
            if(!ann.getTitle().equals(title)){
                continue;
            }
            return ann;
        }
        return null;
    }
    @Deprecated
    public Integer getAnnouncementID(String title){
        return loadedAnnouncements.get(title);
    }
    @Deprecated
    public boolean isAnnouncementLoaded(String title){
        return loadedAnnouncements.containsKey(title);
    }

    public Map<Integer, Announcement> getAnnouncements() {
        return announcements;
    }
    @Deprecated
    public Map<String, Integer> getLoadedAnnouncements() {
        return loadedAnnouncements;
    }
    @Deprecated
    public void unloadAnnouncement(String title){
        loadedAnnouncements.remove(title);
    }

    public void addAnnounement(String title, int id, Announcement announcement){
        announcements.put(id, announcement);
        loadedAnnouncements.put(title, id);
    }

    void saveNewAnnouncement(String title, Announcement announcement, String rawText){
        int ID;
        if(announcements.isEmpty()){
           ID = 1;
        } else {
            ID = Collections.max(announcements.keySet()) + 1;
        }
        announcements.put(ID, announcement);
        loadedAnnouncements.put(title, ID);
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration config = getAnnouncementsFile();
            config.set(title + ".text", rawText);
            config.set(title + ".id", ID);
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy");
            config.set(title + ".date", announcement.getRawDate().format(format));
            if (announcement.getPermission() != null) {
                config.set(title + ".permission", announcement.getPermission());
            }
            try {
                config.save(new File(plugin.getDataFolder() + File.separator + "announcements.yml"));
            } catch (IOException e) {
                plugin.getPluginLogger().logError("Could not save announcements to file!");
                e.printStackTrace();
            }
        });
    }

    public void reloadAnnouncements(){
        announcements.clear();
        loadAnnouncements();
    }

    public FileConfiguration getAnnouncementsFile(){
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
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            announcements.remove(ID);
            loadedAnnouncements.remove(title);
        });
        plugin.getPlayerHandler().removeReadAnnouncement(ID);

        Map<UUID, PlayerData> map = plugin.getPlayerHandler().getPlayers();
        for(UUID uuid : map.keySet()){
            PlayerData data = map.get(uuid);
            if(data == null){
                continue;
            }
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                Player player = Bukkit.getPlayer(uuid);
                if (ann.hasPermission(player)) {
                    data.decreaseUnreadCount();
                }
                data.removeReadAnnouncement(ID);
                new TaskHandler().reloadPlayer(player, data, plugin);
            });
        }
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
                String message = config.getString(s + ".text", "");
                if(message.isEmpty()){
                    continue;
                }

                int id = config.getInt(s + ".id");
                DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy");
                LocalDate date = LocalDate.parse(config.getString(s + ".date"), format);
                String permission = config.getString(s + ".permission");
                if(announcements.put(id, new Announcement(message, s, date, permission)) != null){
                    plugin.getPluginLogger().logError("The messageID of message" + s + " is conflicting with another message, please check announcements.yml!");
                    continue;
                }
                loadedAnnouncements.put(s, id);
            } catch (NullPointerException e){
                plugin.getPluginLogger().logError("Could not load announcement " + s + "!");
                e.printStackTrace();
            }
            catch (DateTimeParseException e){
                plugin.getPluginLogger().logError("Could not load announcement " + s + ", the date is invalid!");
            }
        }
    }
}
