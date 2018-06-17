package nl.raspen0.serverannouncements;

import nl.raspen0.serverannouncements.commands.AnnTabComplete;
import nl.raspen0.serverannouncements.commands.AnnouncementCommand;
import nl.raspen0.serverannouncements.handlers.LangHandler;
import nl.raspen0.serverannouncements.handlers.PlayerDataHandler;
import nl.raspen0.serverannouncements.handlers.PlayerEventHandler;
import nl.raspen0.serverannouncements.handlers.actionbar.ActionBarHandler;
import nl.raspen0.serverannouncements.handlers.actionbar.BukkitActionBarHandler;
import nl.raspen0.serverannouncements.handlers.actionbar.SpigotActionBarHandler;
import nl.raspen0.serverannouncements.handlers.announcement.AnnouncementHandler;
import nl.raspen0.serverannouncements.handlers.announcement.MessageCreator;
import nl.raspen0.serverannouncements.handlers.bossbar.BossBarHandler;
import nl.raspen0.serverannouncements.handlers.chat.ChatHandler;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class ServerAnnouncements extends JavaPlugin {

    private boolean restartTasksOnUpdate = false;

    private LangHandler langHandler;
    private AnnouncementHandler announcementHandler;
    private PlayerDataHandler playerHandler;
    private Logger logger;
    private MessageCreator creator;

    private ChatHandler chatHandler;
    private ActionBarHandler actionBarHandler;
    private BossBarHandler bossBarHandler;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        logger = new Logger(this);

        langHandler = new LangHandler(this);
        announcementHandler = new AnnouncementHandler(this);
        loadHandlers();
        playerHandler = new PlayerDataHandler(this);
        creator = new MessageCreator(this);

        restartTasksOnUpdate = getConfig().getBoolean("restartTasksOnUpdate");

        getCommand("announcements").setExecutor(new AnnouncementCommand(this));
        getCommand("announcements").setTabCompleter(new AnnTabComplete());
        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
        getServer().getPluginManager().registerEvents(creator, this);
    }

    @Override
    public void onDisable(){
        getAnnouncementCreator().removePlayers();
        getPlayerHandler().unloadPlayers();
        if(actionBarHandler != null) {
            getActionBarHandler().unloadPlayers();
        }
        if(bossBarHandler != null) {
            getBossBarHandler().unloadPlayers();
        }
        getLangHandler().unloadMessages();
    }

    public Logger getPluginLogger() {
        return logger;
    }

    public LangHandler getLangHandler() {
        return langHandler;
    }

    public PlayerDataHandler getPlayerHandler() {
        return playerHandler;
    }

    public AnnouncementHandler getAnnouncementHandler() {
        return announcementHandler;
    }

    public MessageCreator getAnnouncementCreator() {
        return creator;
    }

    public boolean shouldRestartTasksOnUpdate() {
        return restartTasksOnUpdate;
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
                getPluginLogger().logMessage("Spigot detected, using SpigotAPI!");
                actionBarHandler = new SpigotActionBarHandler(this);
            } catch (Throwable tr) {
                getPluginLogger().logMessage(ChatColor.AQUA + "Bukkit detected!");
                if (!getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
                    getPluginLogger().logError("ProtocolLib missing!, ActionBar functionality disabled.");
                    return;
                }
                getPluginLogger().logMessage("ProtocolLib detected, using ProtocolLibAPI!");
                actionBarHandler = new BukkitActionBarHandler(this);
            }
        }
    }
}
