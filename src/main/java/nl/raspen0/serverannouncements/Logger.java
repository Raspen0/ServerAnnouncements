package nl.raspen0.serverannouncements;

import org.bukkit.ChatColor;

public class Logger {

    private final ServerAnnouncements plugin;

    public Logger(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public void logToConsole(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[ServerAnnouncements] " + message);
    }

    public void logError(String message){
        plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ServerAnnouncements] " + message);
    }
}
