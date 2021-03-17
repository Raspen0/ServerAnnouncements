package nl.raspen0.serverannouncements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.raspen0.serverannouncements.events.AnnouncementsSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PaperAnnouncementList extends AnnouncementList{

    private final Map<Integer, TextComponent> map;

    public PaperAnnouncementList(int pageSize){
        super(pageSize);
        this.map = new HashMap<>(pageSize);
    }


    @Override
    public boolean addAnnouncement(String message, int annCount, ServerAnnouncements plugin, String date) {
        TextComponent component;
        if (message.contains("url:")) {
            component = createUrlMessage(date, message, plugin);
        } else {
            component = Component.text((date != null ?
                    (ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET) : "") + message);
        }
        map.put(annCount, component);
        return map.size() == pageSize;
    }

    @Override
    public void sendAnnouncements(Player player) {
        AnnouncementsSendEvent event = new AnnouncementsSendEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            for (int i = 0; i < map.size(); i++) {
                player.sendMessage(map.get(i));
            }
        }
    }

    private TextComponent createUrlMessage(String date, String message, ServerAnnouncements plugin) {
        //This is a url:(https://stirebuild.com,link to Stirebuild).
        //Becomes: This is a link to Stirebuild.

        TextComponent messageComponent= Component.text("");
        if (date != null) {
            messageComponent = messageComponent.append(Component.text(ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET));
        }
        messageComponent = messageComponent.color(NamedTextColor.AQUA);

        while (message.contains("url:")){
            int urlStart = message.indexOf("url:");
            int urlEnd = message.indexOf(")", urlStart);
            plugin.getPluginLogger().logDebug("Found URL: Start: " + urlStart + ", End: " + urlEnd);
            messageComponent = messageComponent.append(Component.text(message.substring(0, urlStart)));

            String[] url = message.substring(urlStart + 5, urlEnd).split(",");
            TextComponent linkComponent = Component.text(url[1]).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL,  url[0]))
                    .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, Component.text(url[0])));
            messageComponent = messageComponent.append(linkComponent);
            message = message.substring(urlEnd + 1);
        }
        plugin.getPluginLogger().logDebug("Processed message: " + message);
        messageComponent = messageComponent.append(Component.text(message));

        return messageComponent;
    }

    @Override
    public void sendNextPageMessage(Player player, String[] localizedMessage, String nextPage) {
        TextComponent textComponent = Component.text().content(localizedMessage[0])
                .color(NamedTextColor.AQUA)
                .append(Component.text().content(" /ann " + nextPage + " ").color(NamedTextColor.YELLOW)
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/ann " + nextPage)).build())
                .append(Component.text(localizedMessage[1]).color(NamedTextColor.AQUA)).build();
        player.sendMessage(textComponent);
    }
}
