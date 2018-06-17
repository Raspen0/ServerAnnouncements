package nl.raspen0.serverannouncements.handlers.announcement;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class AnnouncementModifier {

    private final ServerAnnouncements plugin;

    public AnnouncementModifier(ServerAnnouncements plugin){
        this.plugin = plugin;
    }

    public void modifyAnnouncement(CommandSender sender, String title, String change, String value){
        FileConfiguration file = plugin.getAnnouncementHandler().getAnnouncementsFile();
        String text = file.getString(title + ".text");
        final String oldTitle = title;
        if(text == null){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidAnnouncement"));
            return;
        }
        String permission = file.getString(title + ".permission");
        int id = file.getInt(title + ".id");

        if(change.equals("text")){
            file.set(title + "." + change, value);
        }
        if(change.equals("permission")){
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                PlayerData data = plugin.getPlayerHandler().getPlayer(p.getUniqueId());
                if(data == null) {
                    continue;
                }
                boolean hasPermission;
                if(value.equals("none")) {
                    hasPermission = true;
                    value = null;
                } else {
                    hasPermission = p.hasPermission(value);
                }
                //If player had permission before
                if(permission == null || p.hasPermission(permission)){
                    if(!hasPermission){
                        //Not anymore
                        data.decreaseUnreadCount();
                    }
                } else {
                    //If player did not have permission before
                    if(hasPermission){
                        data.increaseUnreadCount();
                    }
                }
                new TaskHandler().reloadPlayer(p, data, plugin);
            }
            file.set(title + "." + change, value);
        }
        if(change.equals("title")){
            file.set(title, null);
            title = value;
            file.set(title + ".text", text);
            file.set(title + ".id", id);
            if(permission != null){
                file.set(title + ".permission", permission);
            }
        }
        plugin.getAnnouncementHandler().addAnnounement(id, new Announcement(title, text, permission));
        try {
            file.save(new File(plugin.getDataFolder() + File.separator + "announcements.yml"));
        } catch (IOException e) {
            plugin.getPluginLogger().logError("Could not save announcements to file!");
            e.printStackTrace();
        }
        sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminModified").replace("{0}", change)
                .replace("{1}", oldTitle));
    }
}
