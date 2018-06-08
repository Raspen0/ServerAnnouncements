package nl.raspen0.serverannouncements.handlers.actionbar;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.entity.Player;

public class SpigotActionBarHandler extends ActionBarHandler{

    private final ServerAnnouncements plugin;

    public SpigotActionBarHandler(ServerAnnouncements plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    void doActionBarTask(Player player, String count) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(plugin.getLangHandler()
                .getMessage(player, "noticeHotBar").replace("{0}", count)));
    }
}
