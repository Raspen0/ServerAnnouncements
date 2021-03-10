package nl.raspen0.serverannouncements;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public interface AnnouncementList {

    boolean addAnnouncement(TextComponent announcement, int annCount);

    boolean isFull();

    void sendAnnouncements(Player player);

    void setTotal(int annTotal);

    void setNextPage();

    boolean hasNextPage();
}
