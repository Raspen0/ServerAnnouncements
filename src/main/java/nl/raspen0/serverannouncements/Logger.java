package nl.raspen0.serverannouncements;

import org.bukkit.ChatColor;

public class Logger {

    private final ServerAnnouncements plugin;

    Logger(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public void logMessage(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[ServerAnnouncements] " + message);
    }

    public void logError(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ServerAnnouncements] " + message);
    }

    public void logDebug(String message){
        if(plugin.getPluginConfig().isDebug()) {
            plugin.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[ServerAnn-Debug] " + message);
        }
    }
}
