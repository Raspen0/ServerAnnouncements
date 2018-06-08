package nl.raspen0.serverannouncements;

import nl.raspen0.serverannouncements.handlers.actionbar.ActionBarHandler;
import nl.raspen0.serverannouncements.handlers.actionbar.BukkitActionBarHandler;
import nl.raspen0.serverannouncements.handlers.actionbar.SpigotActionBarHandler;
import nl.raspen0.serverannouncements.handlers.bossbar.BossBarHandler;
import nl.raspen0.serverannouncements.handlers.chat.ChatHandler;
import nl.raspen0.serverannouncements.commands.AnnouncementCommand;
import nl.raspen0.serverannouncements.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerAnnouncements extends JavaPlugin {

    private LangHandler langHandler;
    private AnnouncementHandler announcementHandler;
    private TaskHandler taskHandler;
    private PlayerDataHandler playerHandler;

    private ChatHandler chatHandler;
    private ActionBarHandler actionBarHandler;
    private BossBarHandler bossBarHandler;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        langHandler = new LangHandler(this);
        announcementHandler = new AnnouncementHandler(this);
        loadHandlers();
        taskHandler = new TaskHandler(this);
        playerHandler = new PlayerDataHandler(this);

        getCommand("announcements").setExecutor(new AnnouncementCommand(this));
        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
    }

    @Override
    public void onDisable(){
        getPlayerHandler().unloadPlayers();
    }

    public LangHandler getLangHandler() {
        return langHandler;
    }

    public TaskHandler getTaskHandler() {
        return taskHandler;
    }

    public PlayerDataHandler getPlayerHandler() {
        return playerHandler;
    }

    public AnnouncementHandler getAnnouncementHandler() {
        return announcementHandler;
    }

    public boolean isChatEnabled(){
        return chatHandler != null;
    }

    public boolean isBossBarEnabled(){
        return bossBarHandler != null;
    }

    public boolean isActionBarEnabled(){
        return actionBarHandler != null;
    }

    public ActionBarHandler getActionBarHandler() {
        return actionBarHandler;
    }

    public BossBarHandler getBossBarHandler() {
        return bossBarHandler;
    }

    public ChatHandler getChatHandler() {
        return chatHandler;
    }

    private void loadHandlers(){
        if(getConfig().getBoolean("notification.bossbar.enabled")){
            bossBarHandler = new BossBarHandler(this);
        }
        if(getConfig().getBoolean("notification.chat.enabled")){
            chatHandler = new ChatHandler(this);
        }
        if(getConfig().getBoolean("notification.actionbar.enabled")) {
            try {
                Class.forName("org.spigotmc.SpigotConfig");
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Spigot detected, using SpigotAPI!");
                actionBarHandler = new SpigotActionBarHandler(this);
            } catch (Throwable tr) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "Bukkit detected!");
                if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "ProtocolLib missing!, ActionBar functionality disabled.");
                    return;
                }
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "ProtocolLib detected, using ProtocolLibAPI!");
                actionBarHandler = new BukkitActionBarHandler(this);
            }
        }
    }

    public void reloadData(CommandSender sender){
        sender.sendMessage(getLangHandler().getMessage(sender, "announceReload"));
        getAnnouncementHandler().reloadAnnouncements();
        getPlayerHandler().unloadPlayers();
        for(Player player : getServer().getOnlinePlayers()){
            getServer().getScheduler().runTaskAsynchronously(this, () -> {
                PlayerData data = getPlayerHandler().loadPlayer(player);
                getTaskHandler().startTasks(player, data, data.getCount());
            });
        }
        sender.sendMessage(getLangHandler().getMessage(sender, "announceReloaded"));
    }
}
