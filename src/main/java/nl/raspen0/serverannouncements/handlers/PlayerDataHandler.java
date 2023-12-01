package nl.raspen0.serverannouncements.handlers;

import lombok.Getter;
import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataHandler {

    //Players are only added to the map if they have unread announcements.
    @Getter
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private final ServerAnnouncements plugin;

    public PlayerDataHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    /**
     * Gets the PlayerData of given player.
     *
     * @param id The UUID of the player.
     *
     * @return The PlayerData.
     */
    public PlayerData getPlayer(UUID id){
        return players.get(id);
    }

    /**
     * Check if the given player has unread announcements.
     *
     * @param id The UUID of the player.
     *
     * @return true if the player has unread announcements.
     */
    public boolean hasUnreadAnnouncements(UUID id){
        return players.containsKey(id);
    }

    /**
     * Add a player who has unread announcements to the map.
     *
     * @param uuid The UUID of the player.
     * @param data The data of the player.
     */
    void addPlayer(UUID uuid, PlayerData data){
        players.put(uuid, data);
    }

    /**
     * Loads the PlayerData for the given player.
     *
     * @param player The player.
     *
     * @return The PlayerData of the give player.
     */
    public PlayerData loadPlayer(Player player){
        UUID uuid = player.getUniqueId();
        FileConfiguration file = loadDataFile();
        List<Integer> list;
        if(file.contains(uuid.toString())){
            list = file.getIntegerList(uuid + ".read");
        } else {
            list = new ArrayList<>();
        }
        return new PlayerData(list, getCount(list, player));
    }

    /**
     * Calculates the unread announcement count.
     *
     * @param list The list with announcement ID's.
     * @param player The player whose count should be calculated.
     *
     * @return The unread announcement count.
     */
    private int getCount(List<Integer> list, Player player){
        int count = 0;
        for(int i : plugin.getAnnouncementHandler().getAnnouncements().keySet()){
            if(list.contains(i)){
                continue;
            }
            if (!plugin.getAnnouncementHandler().getAnnouncement(i).hasPermission(player)) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * Unloads the data of the give player.
     * Used when player has no unread announcements anymore.
     */
    void unloadPlayer(UUID uuid){
        if(players.containsKey(uuid)) {
            players.get(uuid).clearTasks();
            players.remove(uuid);
        }
    }

    /**
     * Unloads all the player announcement data.
     * Used when disabling or reloading the plugin.
     */
    public void unloadPlayers(){
        Iterator<Map.Entry<UUID, PlayerData>> entries = players.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<UUID, PlayerData> entry = entries.next();
            entry.getValue().clearTasks();
            entries.remove();
        }
    }

    /**
     * Remove the given Announcement ID from the data of all players.
     *
     * @param id The Announcement ID.
     */
    public void removeReadAnnouncement(int id){
        FileConfiguration file = loadDataFile();
        for(String uuid : file.getKeys(false)) {
            List<Integer> list = file.getIntegerList(uuid + ".read");
            if (list.contains(id)) {
                list.remove(Integer.valueOf(id));
                file.set(uuid + ".read", list);
            }
        }
        try {
            file.save(new File(plugin.getDataFolder() + File.separator + "data.yml"));
        } catch (IOException e) {
            plugin.getPluginLogger().logError("Could not save playerdata to file!");
            e.printStackTrace();
        }
    }

    /**
     * Loads the data.yml file that contains the read announcement data.
     *
     * @return The FileConfiguration of the data file.
     */
    private FileConfiguration loadDataFile(){
        File file = new File(plugin.getDataFolder() + File.separator + "data.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getPluginLogger().logError("Could not create data.yml!");
                e.printStackTrace();
            }
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the read announcements of the given player.
     *
     * @param uuid The UUID of the player.
     * @param list The list containing Announcement ID's.
     */
    public void saveReadAnnouncements(UUID uuid, List<Integer> list){
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration file = loadDataFile();
            file.set(uuid.toString(), list);
            try {
                file.save(new File(plugin.getDataFolder() + File.separator + "data.yml"));
            } catch (IOException e) {
                plugin.getPluginLogger().logError("Could not save playerdata to file!");
                e.printStackTrace();
            }
        });
    }

    /**
     * Marks all the available announcements of the player as read.
     *
     * @param player The player.
     */
    public void setReadAnnouncements(Player player){
        UUID uuid = player.getUniqueId();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration file = loadDataFile();
            List<Integer> list = new ArrayList<>();
            for (int id : plugin.getAnnouncementHandler().getAnnouncements().keySet()) {
                if (!plugin.getAnnouncementHandler().getAnnouncement(id).hasPermission(player)) {
                    continue;
                }
                list.add(id);
            }
            file.set(uuid.toString() + ".read", list);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                //getPlayer(uuid).setReadAnnouncement(list);
                //getPlayer(uuid).clearTasks();
                unloadPlayer(uuid);

                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        file.save(new File(plugin.getDataFolder() + File.separator + "data.yml"));
                    } catch (IOException e) {
                        plugin.getPluginLogger().logError("Could not save playerdata to file!");
                        e.printStackTrace();
                    }
                });
            });
        });
    }
}
