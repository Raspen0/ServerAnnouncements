package nl.raspen0.serverannouncements;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    private MessageUtils(){}

    public static void sendLocalisedMessage(String messageID, CommandSender sender, ServerAnnouncements plugin){
        Component message = plugin.getLangHandler().getMessage(sender, messageID);
        ServerAnnouncements.getAudiences().sender(sender).sendMessage(message);
    }

    public static void sendMessage(Component message, CommandSender sender){
        ServerAnnouncements.getAudiences().sender(sender).sendMessage(message);
    }

    private static TextComponent createUrlMessage(String message, ServerAnnouncements plugin) {
        //This is a url:(https://stirebuild.com,link to Stirebuild).
        //Becomes: This is a link to Stirebuild.

        TextComponent messageComponent= Component.text("- ");

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

    public static TextComponent getComponent(String message, ServerAnnouncements plugin) {
        //   TextComponent messageComponent = Component.text("");
        //   if (date != null && plugin.getPluginConfig().showDate()) {
        //      messageComponent = messageComponent.append(Component.text(ChatColor.AQUA + "[" + ChatColor.YELLOW + date + ChatColor.AQUA + "]" + ChatColor.RESET));
        //   }
        if (message.contains("url:")) {
            return createUrlMessage(message, plugin);
            // messageComponent = messageComponent.append(createUrlMessage(message, plugin));
        } else {
            return Component.text("- " + message);
            // messageComponent = messageComponent.append(Component.text(message));
        }
        //    return messageComponent;
        //TODO: Convert colors here.
    }

    public static void sendNextPageMessage(int nextPage, Player player, ServerAnnouncements plugin) {
        Component nextPageMessage = plugin.getLangHandler().getMessage(player, "announceNextPage").replaceText(TextReplacementConfig.builder()
                        .matchLiteral("{0}").replacement(Component.text().content(" /ann " + nextPage + " ").color(NamedTextColor.YELLOW)
                                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/ann " + nextPage))
                                .hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, plugin.getLangHandler().getMessage(player, "announceNextPageTooltip"))))
                .build());
        ServerAnnouncements.getAudiences().player(player).sendMessage(nextPageMessage);
    }

}
