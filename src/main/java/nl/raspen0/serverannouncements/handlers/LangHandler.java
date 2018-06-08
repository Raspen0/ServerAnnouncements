package nl.raspen0.serverannouncements.handlers;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangHandler {

    private Map<String, String> messages = new HashMap<>();
    private List<String> loadedLangs = new ArrayList<>();
    private final ServerAnnouncements plugin;
    private boolean multiLanguage;

    public LangHandler(ServerAnnouncements plugin){
        this.plugin = plugin;
        multiLanguage = plugin.getConfig().getBoolean("language.multiLanguage");
        loadMessages();
    }

    public String getPlayerLanguage(CommandSender sender){
        if(!multiLanguage){
            return loadedLangs.get(0);
        }
        for(String lang : loadedLangs){
            if(sender.hasPermission("serverann.lang." + lang)){
                return lang;
            }
        }
        System.out.println("No Lang!");
        return loadedLangs.get(0);
    }

    public String getMessage(CommandSender sender, String ID){
        return getMessage(getPlayerLanguage(sender), ID);
    }

    public String getMessage(String language, String ID){
        try{
            return messages.get(language + ID);
        } catch (NullPointerException e){
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not find language " + language + "!");
            return messages.get("eng" + ID);
        }
    }

    private void loadMessages(){
        loadedLangs = plugin.getConfig().getStringList("language.languages");
        int count = 0;
        for(String lang : loadedLangs){
            if(!multiLanguage){
                if(count == 1){
                    return;
                }
            }
            try {
                YamlConfiguration file = new YamlConfiguration();
                file.load(new InputStreamReader(plugin.getResource("messages_" + lang + ".yml")));
                for(String message : file.getKeys(false)){
                    messages.put(lang + message, ChatColor.translateAlternateColorCodes('&', file.getString(message)));
                }
                count++;
            } catch (IOException | InvalidConfigurationException e) {
                plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not find language " + lang + "!");
            }
        }
    }
}
