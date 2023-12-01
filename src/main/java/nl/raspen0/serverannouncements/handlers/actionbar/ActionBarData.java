package nl.raspen0.serverannouncements.handlers.actionbar;

import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
class ActionBarData {

    private int actionTimer;
    private final int actionTaskID;

    ActionBarData(int task){
        this.actionTaskID = task;
    }

    void cancelTask(){
        Bukkit.getServer().getScheduler().cancelTask(actionTaskID);
    }

    void increaseTimer() {
        actionTimer++;
    }

}
