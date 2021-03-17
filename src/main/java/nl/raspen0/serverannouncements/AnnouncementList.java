package nl.raspen0.serverannouncements;

import org.bukkit.entity.Player;

public abstract class AnnouncementList {

    protected boolean nextPage = false;
    protected int pageSize;

    public AnnouncementList(int pageSize){
        this.pageSize = pageSize;
    }

    public boolean addAnnouncement(String announcement,  int annCount, ServerAnnouncements plugin){
        return addAnnouncement(announcement, annCount, plugin, null);
    }

    public abstract boolean addAnnouncement(String announcement, int annCount, ServerAnnouncements plugin, String date);

    public abstract void sendAnnouncements(Player player);

    public abstract void sendNextPageMessage(Player player, String[] localizedMessages, String nextPage);

    public void setTotal(int annTotal) {
        this.pageSize = annTotal;
    }

    public void setNextPage() {
        this.nextPage = true;
    }

    public boolean hasNextPage() {
        return nextPage;
    }

}
