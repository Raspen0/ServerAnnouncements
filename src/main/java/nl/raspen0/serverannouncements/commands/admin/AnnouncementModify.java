package nl.raspen0.serverannouncements.commands.admin;

import nl.raspen0.serverannouncements.PlayerData;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.handlers.TaskHandler;
import nl.raspen0.serverannouncements.handlers.announcement.Announcement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AnnouncementModify implements AdminCommand{

    @Override
    public void runCommand(CommandSender sender, String[] args, ServerAnnouncements plugin) {
        if(!sender.hasPermission("serverann.admin.modify")){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
            return;
        }
        if (args.length < 4) {
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "notEnoughArgs"));
            return;
        }
        String change = args[2].toLowerCase();
        if (!change.equals("text") && !change.equals("permission") && !change.equals("title")) {
            ChatColor YELLOW = ChatColor.YELLOW;
            ChatColor RED = ChatColor.RED;
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminValidArgs") + YELLOW + "text" + RED +
                    ", " + YELLOW + "title" + RED + ", " + "permission" + RED + ".");
            return;
        }
        String title = args[3];
        modifyAnnouncement(sender, title, change, args[4], plugin);
    }

    //TODO: Optimize
    private void modifyAnnouncement(CommandSender sender, String title, String change, String value, ServerAnnouncements plugin){
        FileConfiguration file = plugin.getAnnouncementHandler().getAnnouncementsFile();
        String text = file.getString(title + ".text");
        final String oldTitle = title;
        if(text == null){
            sender.sendMessage(plugin.getLangHandler().getMessage(sender, "adminInvalidAnnouncement"));
            return;
        }
        String permission = file.getString(title + ".permission");
        int id = file.getInt(title + ".id");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate date = LocalDate.parse(file.getString(title + ".date"), format);

        if(change.equals("text")){
            if(!sender.hasPermission("serverann.admin.modify.text")){
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
                return;
            }
            file.set(title + "." + change, value);
        }
        if(change.equals("permission")){
            if(!sender.hasPermission("serverann.admin.modify.permission")){
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
                return;
            }
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
            if(!sender.hasPermission("serverann.admin.modify.title")){
                sender.sendMessage(plugin.getLangHandler().getMessage(sender, "noPerm"));
                return;
            }
            plugin.getAnnouncementHandler().removeLoadedAnnouncement(title);
            file.set(title, null);
            title = value;
            file.set(title + ".text", text);
            file.set(title + ".id", id);
            if(permission != null){
                file.set(title + ".permission", permission);
            }
        }
        plugin.getAnnouncementHandler().addAnnounement(title, id, new Announcement(ChatColor.translateAlternateColorCodes('&', text), date, permission));
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
