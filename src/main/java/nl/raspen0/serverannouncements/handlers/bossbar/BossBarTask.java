package nl.raspen0.serverannouncements.handlers.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

class BossBarTask {

    private final int task;
    private final BossBar bar;

    BossBarTask(int task, BossBar bar){
        this.task = task;
        this.bar = bar;
    }

    void removeBossBar(Player player){
        bar.removePlayer(player);
    }

    void clearBossbarTask(){
        Bukkit.getServer().getScheduler().cancelTask(task);
    }
}
