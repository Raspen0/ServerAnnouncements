package nl.raspen0.serverannouncements;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {

    private List<Integer> tasks;
    private List<Integer> readAnnouncements;
    private int actionbarTimer;
    private int actionbarTask;
    private int bossbarTask;
    private int count;

    public PlayerData(List<Integer> readAnnouncements, int count){
        this.readAnnouncements = readAnnouncements;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public List<Integer> getReadAnnouncements() {
        return readAnnouncements;
    }

    public void setReadAnnouncement(List<Integer> list){
        readAnnouncements = list;
    }

    public int getActionbarTimer() {
        return actionbarTimer;
    }

    public void clearActionbarTimer(){
        Bukkit.getServer().getScheduler().cancelTask(actionbarTask);
        actionbarTimer = 0;
        actionbarTask = 0;
    }

    public void increaseActionbarTimer() {
        actionbarTimer++;
    }

    public int getActionbarTask() {
        return actionbarTask;
    }

    public void setActionbarTask(int actionbarTask) {
        this.actionbarTask = actionbarTask;
    }

    public int getBossbarTask() {
        return bossbarTask;
    }

    public void setBossbarTask(int bossbarTask) {
        this.bossbarTask = bossbarTask;
    }

    public void clearBossbarTask(){
        Bukkit.getServer().getScheduler().cancelTask(bossbarTask);
        bossbarTask = 0;
    }

    public void addTask(int task){
        if(tasks == null){
            tasks = new ArrayList<>();
        }
        tasks.add(task);
    }

    public void clearTasks(){
        if(tasks == null){
            return;
        }
        for(int i : tasks){
            Bukkit.getServer().getScheduler().cancelTask(i);
        }
        tasks.clear();
        tasks = null;
    }

}
