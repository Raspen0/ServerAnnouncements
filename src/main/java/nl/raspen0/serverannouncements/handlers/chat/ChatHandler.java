package nl.raspen0.serverannouncements.handlers.chat;

import nl.raspen0.serverannouncements.events.NoticeType;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.NoticeSendEvent;
import org.bukkit.Bukkit;
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

    public int startChatTask(Player player){
        if(repeat > 0){
            return plugin.getServer().getScheduler().runTaskTimer(plugin, () -> doChatTask(player), delay * 20, repeat * 20).getTaskId();
        } else {
            return plugin.getServer().getScheduler().runTaskLater(plugin, () -> doChatTask(player), delay * 20).getTaskId();
        }
    }

    private void doChatTask(Player player){
        NoticeSendEvent event = new NoticeSendEvent(player, plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getUnreadCount(), NoticeType.CHAT);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            player.sendMessage(plugin.getLangHandler().getMessage(player, "noticeChat").replace("{0}", String.valueOf(event.getUnreadCount())));
        }
    }
}
