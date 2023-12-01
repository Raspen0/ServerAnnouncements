package nl.raspen0.serverannouncements.handlers.bossbar;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import nl.raspen0.serverannouncements.ServerAnnouncements;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class BossBarTask {

    private final int task;
    @Getter
    private final BossBar bar;

    BossBarTask(int task, BossBar bar){
        this.task = task;
        this.bar = bar;
    }

    void removeBossBar(Player player){
        ServerAnnouncements.getAudiences().player(player).hideBossBar(bar);
    }

    void clearBossbarTask(){
        Bukkit.getServer().getScheduler().cancelTask(task);
    }

}
