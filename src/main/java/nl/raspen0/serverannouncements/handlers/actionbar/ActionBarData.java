package nl.raspen0.serverannouncements.handlers.actionbar;

import org.bukkit.Bukkit;

class ActionBarData {

    private int actionTimer;
    private final int actionTask;

    ActionBarData(int task){
        this.actionTask = task;
    }

    int getTask() {
        return actionTask;
    }

    int getTimer() {
        return actionTimer;
    }

    void cancelTask(){
        Bukkit.getServer().getScheduler().cancelTask(actionTask);
    }

    void increaseTimer() {
        actionTimer++;
    }

}
