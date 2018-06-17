package nl.raspen0.serverannouncements.handlers.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.raspen0.serverannouncements.events.NoticeType;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import nl.raspen0.serverannouncements.events.NoticeSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpigotActionBarHandler extends ActionBarHandler{

    private final ServerAnnouncements plugin;

    public SpigotActionBarHandler(ServerAnnouncements plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    void doActionBarTask(Player player) {
        NoticeSendEvent event = new NoticeSendEvent(player, plugin.getPlayerHandler().getPlayer(player.getUniqueId()).getUnreadCount(),
                NoticeType.ACTIONBAR);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getLangHandler()
                .getMessage(player, "noticeHotBar").replace("{0}", String.valueOf(event.getUnreadCount()))));
    }
}
