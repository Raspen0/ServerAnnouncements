package nl.raspen0.serverannouncements;

import org.bukkit.entity.Player;

public interface AnnouncementList {

    boolean addAnnouncement(Object announcement, int annCount);

    boolean isFull();

    void sendAnnouncements(Player player);

    void setTotal(int annTotal);

    void setNextPage();

    boolean hasNextPage();
}
