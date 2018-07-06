package nl.raspen0.serverannouncements;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BukkitAnnouncementList implements AnnouncementList{

    private Map<Integer, String> map;
    private final int pageSize;

    public BukkitAnnouncementList(int pageSize){
        this.pageSize = pageSize;
        this.map = new HashMap<>(pageSize);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
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
        for(int i = 1; i < map.size() + 1; i++){
            player.sendMessage(map.get(i));
        }
    }
}
