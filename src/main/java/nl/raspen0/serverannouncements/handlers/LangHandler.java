package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LangHandler {

    private Map<String, String> messages = new HashMap<>();
    private final ServerAnnouncements plugin;

    public LangHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        loadMessages();
    }

    public String getMessage(String ID){
        return messages.get(ID);
    }

    private void loadMessages(){
        try {
            YamlConfiguration file = new YamlConfiguration();
            file.load(new InputStreamReader(plugin.getResource("messages.yml")));
            for(String s : file.getKeys(false)){
                messages.put(s, ChatColor.translateAlternateColorCodes('&', file.getString(s)));
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}
