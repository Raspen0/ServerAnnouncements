package nl.raspen0.serverannouncements;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SpigotAnnouncementList implements AnnouncementList{

    private Map<Integer, TextComponent> map;
    private final int pageSize;

    public SpigotAnnouncementList(int pageSize){
        this.pageSize = pageSize;
        this.map = new HashMap<>(pageSize);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean addAnnouncement(Object announcement, int annCount) {
        if(announcement instanceof TextComponent){
            map.put(annCount, (TextComponent) announcement);

        } else if(announcement instanceof String){
            map.put(annCount, new TextComponent((String) announcement));
        }
        return isFull();
    }

    @Override
    public boolean isFull() {
        return map.size() == pageSize;
    }

    @Override
    public void sendAnnouncements(Player player) {
        for(int i = 1; i < map.size() + 1; i++){
            player.spigot().sendMessage(map.get(i));
        }
    }
}
