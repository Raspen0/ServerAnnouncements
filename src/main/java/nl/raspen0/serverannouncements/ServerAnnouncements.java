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

    private Config config;
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
        if(!isSpigot()){
            getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unsupported server implementation, please use Spigot or a fork based on it.");
            getPluginLoader().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        loadConfig();
        logger = new Logger(this);
        langHandler = new LangHandler(this);
        announcementHandler = new AnnouncementHandler(this);
        loadHandlers();
        playerHandler = new PlayerDataHandler(this);
        creator = new MessageCreator(this);

        getCommand("announcements").setExecutor(new AnnouncementCommand(this));
        getCommand("announcements").setTabCompleter(new AnnTabComplete(this));
        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
        getServer().getPluginManager().registerEvents(creator, this);
    }

    @Override
    public void onDisable(){
        getPlayerHandler().unloadPlayers();
        getAnnouncementCreator().removePlayers();
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

    public Config getPluginConfig() {
        return config;
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

    public void loadConfig(){
        config = new Config(getConfig());
    }

    private void loadHandlers(){
        if(getConfig().getBoolean("notification.bossbar.enabled")){
            getPluginLogger().logDebug("Loading BossBarHandler.");
            bossBarHandler = new BossBarHandler(this);
        }
        if(getConfig().getBoolean("notification.chat.enabled")){
            getPluginLogger().logDebug("Loading ChatHandler.");
            chatHandler = new ChatHandler(this);
        }
        if(getConfig().getBoolean("notification.actionbar.enabled")) {
            actionBarHandler = new SpigotActionBarHandler(this);
        }
    }

    public boolean isSpigot(){
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return true;
        } catch (Throwable tr) {
            return  false;
        }
    }
}
