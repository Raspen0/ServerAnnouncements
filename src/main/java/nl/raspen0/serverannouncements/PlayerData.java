package nl.raspen0.serverannouncements;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.List;

public class PlayerData {

    @Setter
    private List<Integer> tasks;
    @Getter @Setter
    private List<Integer> readAnnouncements;
    @Getter
    private int unreadCount;

    public PlayerData(List<Integer> readAnnouncements, int count){
        this.readAnnouncements = readAnnouncements;
        this.unreadCount = count;
    }

    public void increaseUnreadCount(){
        unreadCount++;
    }

    public void decreaseUnreadCount(){
        unreadCount--;
    }

    public void removeReadAnnouncement(int id){
        readAnnouncements.remove(Integer.valueOf(id));
    }

    public void clearTasks(){
        if(tasks == null){
            return;
        }
        for(int i : tasks){
            Bukkit.getServer().getScheduler().cancelTask(i);
        }
        tasks.clear();
    }

}
