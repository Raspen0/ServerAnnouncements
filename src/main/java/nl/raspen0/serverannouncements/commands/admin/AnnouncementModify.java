package nl.raspen0.serverannouncements.commands.admin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nl.raspen0.serverannouncements.MessageUtils;
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
            MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
            return;
        }
        if (args.length < 4) {
            MessageUtils.sendLocalisedMessage("notEnoughArgs", sender, plugin);
            return;
        }
        String change = args[2].toLowerCase();
        if (!change.equals("text") && !change.equals("permission") && !change.equals("title")) {
            MessageUtils.sendLocalisedMessage("adminValidArgs", sender, plugin);
            return;
        }
        String title = args[3];
        StringBuilder builder = new StringBuilder();
        for(int i = 4; i < args.length; i++){
            builder.append(args[i]).append(" ");
        }
        modifyAnnouncement(sender, title, change, builder.toString(), plugin);
    }

    private void modifyAnnouncement(CommandSender sender, String title, String change, String value, ServerAnnouncements plugin){
        FileConfiguration file = plugin.getAnnouncementHandler().getAnnouncementsFile();
        String text = file.getString(title + ".text");
        final String oldTitle = title;
        if(text == null){
            MessageUtils.sendLocalisedMessage("adminInvalidAnnouncement", sender, plugin);
            return;
        }
        String permission = file.getString(title + ".permission");
        int id = file.getInt(title + ".id");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yy");
        LocalDate date = LocalDate.parse(file.getString(title + ".date"), format);

        if(change.equals("text")){
            if(!sender.hasPermission("serverann.admin.modify.text")){
                MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
                return;
            }
            file.set(title + "." + change, value);
            text = value;
        }
        if(change.equals("permission")){
            if(!sender.hasPermission("serverann.admin.modify.permission")){
                MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
                return;
            }
            if(value.equals("none")) {
                value = null;
            }
            updatePlayerCounts(permission, value, plugin);
            file.set(title + "." + change, value);
            permission = value;
        }
        if(change.equals("title")){
            if(!sender.hasPermission("serverann.admin.modify.title")){
                MessageUtils.sendLocalisedMessage("noPerm", sender, plugin);
                return;
            }
            plugin.getAnnouncementHandler().unloadAnnouncement(title);
            file.set(title, null);
            title = value;
            file.set(title + ".text", text);
            file.set(title + ".id", id);
            if(permission != null){
                file.set(title + ".permission", permission);
            }
            title = value;
        }
        plugin.getAnnouncementHandler().addAnnounement(title, id, new Announcement(
                text, title, date, permission));
        try {
            file.save(new File(plugin.getDataFolder() + File.separator + "announcements.yml"));
        } catch (IOException e) {
            plugin.getPluginLogger().logError("Could not save announcements to file!");
            e.printStackTrace();
        }
        MessageUtils.sendMessage(plugin.getLangHandler().getMessage(sender, "adminModified")
                .replaceText(TextReplacementConfig.builder().matchLiteral("{0}").replacement(change).build())
                .replaceText(TextReplacementConfig.builder().matchLiteral("{1}").replacement(oldTitle).build()), sender);
    }

    private void updatePlayerCounts(String permission, String value, ServerAnnouncements plugin){
        for(Player p : plugin.getServer().getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerHandler().getPlayer(p.getUniqueId());
            if(data == null) {
                data = plugin.getPlayerHandler().loadPlayer(p);
            }
            boolean hasPermission;
            if(value == null) {
                hasPermission = true;
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
    }
}
