package nl.raspen0.serverannouncements;

import nl.raspen0.serverannouncements.events.AnnouncementsSendEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BukkitAnnouncementList implements AnnouncementList{

    private Map<Integer, String> map;
    private boolean nextPage = false;
    private int pageSize;

    public BukkitAnnouncementList(int pageSize){
        this.pageSize = pageSize;
        this.map = new HashMap<>(pageSize);
    }

    @Override
    public boolean isFull() {
        return map.size() == pageSize;
    }

    @Override
    public boolean addAnnouncement(Object announcement, int annCount) {
        if(!(announcement instanceof String)){
            return false;
        }
        map.put(annCount, (String) announcement);
        return isFull();
    }

    @Override
    public void sendAnnouncements(Player player) {
        AnnouncementsSendEvent event = new AnnouncementsSendEvent(player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            for (int i = 1; i < map.size() + 1; i++) {
                player.sendMessage(map.get(i));
            }
        }
    }

    @Override
    public void setTotal(int annTotal) {
        this.pageSize = annTotal;
    }

    @Override
    public void setNextPage() {
        this.nextPage = true;
    }

    @Override
    public boolean hasNextPage() {
        return nextPage;
    }
}
