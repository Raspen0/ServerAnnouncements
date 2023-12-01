package nl.raspen0.serverannouncements;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import nl.raspen0.serverannouncements.events.AnnouncementsSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AnnouncementList {

    protected boolean nextPage = false;

    private final Map<Integer, Component> map;

    public AnnouncementList(int pageSize){
        this.map = new HashMap<>(pageSize);
    }

    public void addAnnouncement(String announcement, int annCount, ServerAnnouncements plugin, Player player, String date) {
        System.out.println(announcement);
        MiniMessage minimessage = MiniMessage.builder()
                .tags(TagResolver.builder()
                        .resolver(StandardTags.defaults())
                        .resolver(papiTag(player))
                        .build()
                )
                .build();
        Component messageComponent = minimessage.deserialize(announcement);
        if (date != null && plugin.getPluginConfig().showDate()) {
            Component dateComponent = Component.text(NamedTextColor.AQUA + "[" +
                    NamedTextColor.YELLOW + date + NamedTextColor.AQUA + "]" + NamedTextColor.WHITE);
            //messageComponent = dateComponent.append(messageComponent);
        }
        map.put(annCount, messageComponent);
    }

    public void addAnnouncement(String announcement, int annCount, ServerAnnouncements plugin, Player player){
        addAnnouncement(announcement, annCount, plugin, player, null);
    }

    public void sendAnnouncements(Player player) {
        AnnouncementsSendEvent event = new AnnouncementsSendEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            for (Map.Entry<Integer, Component> entry : map.entrySet()) {
                MessageUtils.sendMessage(entry.getValue(), player);
            }
        }
    }

    public void setNextPage() {
        this.nextPage = true;
    }

    public boolean hasNextPage() {
        return nextPage;
    }

    public @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player.
            final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

            // We need to turn this ugly legacy string into a nice component.
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.selfClosingInserting(componentPlaceholder);
        });
    }

}
