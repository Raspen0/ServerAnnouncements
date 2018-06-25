package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class LangHandler {

    private Map<String, String> messages = new HashMap<>();
    private List<String> loadedLangs = new ArrayList<>();
    private final ServerAnnouncements plugin;


    public LangHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        loadMessages();
    }

    private String getPlayerLanguage(CommandSender sender){
        if(!plugin.getPluginConfig().multiLanguage()){
            return loadedLangs.get(0);
        }
        for(String lang : loadedLangs){
            if(sender.hasPermission("serverann.lang." + lang)){
                return lang;
            }
        }
        plugin.getPluginLogger().logError("Could not find language for " + sender.getName());
        return loadedLangs.get(0);
    }

    public String getMessage(CommandSender sender, String ID){
        return getMessage(getPlayerLanguage(sender), ID);
    }

    public String getMessage(String language, String ID){
        try{
            return messages.get(language + ID);
        } catch (NullPointerException e){
            plugin.getPluginLogger().logError("Could not find language " + language + "!");
            return messages.get("eng" + ID);
        }
    }

    public void unloadMessages(){
        messages.clear();
        messages = null;

        loadedLangs.clear();
        loadedLangs = null;
    }

    public void reloadMessages(){
        messages.clear();
        loadedLangs.clear();
        loadMessages();
    }

    private void loadMessages(){
        loadedLangs = plugin.getConfig().getStringList("language.languages");
        int count = 0;
        for(String lang : loadedLangs){
            if(!plugin.getPluginConfig().multiLanguage()){
                if(count == 1){
                    return;
                }
            }
            try {
                YamlConfiguration fileConfiguration = new YamlConfiguration();
                File file = new File(plugin.getDataFolder() + File.separator + "messages_" + lang + ".yml");
                if(file.exists()){
                    plugin.getPluginLogger().logMessage("Loading language " + lang + " from plugin directory.");
                    fileConfiguration.load(file);
                } else {
                    plugin.getPluginLogger().logMessage("Loading language " + lang + " from jar.");
                    fileConfiguration.load(new InputStreamReader(plugin.getResource("messages_" + lang + ".yml")));
                }

                for(String message : fileConfiguration.getKeys(false)){
                    messages.put(lang + message, ChatColor.translateAlternateColorCodes('&', fileConfiguration.getString(message)));
                }
                count++;
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getPluginLogger().logError("Could not find language " + lang + "!");
            }
        }
    }
}
