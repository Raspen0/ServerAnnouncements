package nl.raspen0.serverannouncements;

import org.bukkit.Bukkit;

import java.util.List;

public class PlayerData {

    private List<Integer> tasks;
    private List<Integer> readAnnouncements;
    private int count;

    public PlayerData(List<Integer> readAnnouncements, int count){
        this.readAnnouncements = readAnnouncements;
        this.count = count;
    }

    public int getUnreadCount() {
        return count;
    }

    public void increaseUnreadCount(){
        count++;
    }

    public void decreaseUnreadCount(){
        count--;
    }

    public List<Integer> getReadAnnouncements() {
        return readAnnouncements;
    }

    public void setReadAnnouncement(List<Integer> list){
        readAnnouncements = list;
    }

    public void removeReadAnnouncement(int id){
        readAnnouncements.remove(Integer.valueOf(id));
    }

    public void setTasks(List<Integer> list){
        tasks = list;
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
