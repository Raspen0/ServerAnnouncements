package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataHandler {

    private Map<UUID, PlayerData> players = new HashMap<>();
    private final ServerAnnouncements plugin;

    public PlayerDataHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public PlayerData getPlayer(UUID id){
        return players.get(id);
    }

    void addPlayer(UUID uuid, PlayerData data){
        players.put(uuid, data);
    }

    public Map<UUID, PlayerData> getPlayers(){
        return players;
    }

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

    void unloadPlayer(UUID uuid){
        if(players.containsKey(uuid)) {
            players.get(uuid).clearTasks();
            players.remove(uuid);
        }
    }

    public void unloadPlayers(){
        for(UUID uuid : players.keySet()){
            unloadPlayer(uuid);
        }
    }

    public void removeReadAnnouncement(int id){
        FileConfiguration file = loadDataFile();
        for(String uuid : file.getKeys(false)) {
            List<Integer> list = file.getIntegerList(uuid + ".read");
            if (list.contains(id)) {
                list.remove(id);
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
                getPlayer(uuid).setReadAnnouncement(list);
                getPlayer(uuid).clearTasks();
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
