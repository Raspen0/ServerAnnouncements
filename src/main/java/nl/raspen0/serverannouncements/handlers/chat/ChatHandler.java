package nl.raspen0.serverannouncements.handlers.chat;

import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

public class ChatHandler {

    private final ServerAnnouncements plugin;
    private int delay;
    private int repeat;

    public ChatHandler(ServerAnnouncements plugin) {
        this.plugin = plugin;
        delay = plugin.getConfig().getInt("notification.chat.delay");
        repeat = plugin.getConfig().getInt("notification.chat.repeat");
    }

    public int startChatTask(Player player, String count){
        if(repeat > 0){
            return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> player.sendMessage(
                    plugin.getLangHandler().getMessage(player, "noticeChat").replace("{0}", count)), delay * 20, repeat * 20).getTaskId();
        } else {
            return plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.sendMessage(
                    plugin.getLangHandler().getMessage(player, "noticeChat").replace("{0}", count)), delay * 20).getTaskId();
        }
    }
}
