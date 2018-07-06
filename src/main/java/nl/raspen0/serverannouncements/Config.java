package nl.raspen0.serverannouncements;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private boolean restartTasksOnUpdate;
    private boolean showDate;
    private boolean multiLanguage;
    private boolean debug;

    private String hoverMessage;
    private int announcementsPerPage;

    Config(FileConfiguration config){
        this.restartTasksOnUpdate = config.getBoolean("restartTasksOnUpdate");
        this.showDate = config.getBoolean("announcements.showDate");
        this.multiLanguage = config.getBoolean("language.multiLanguage");
        this.announcementsPerPage = config.getInt("announcements.announcementsPerPage");
        this.debug = config.getBoolean("debug.logging");
        this.hoverMessage = config.getString("announcements.hoverMessage");
    }

    public boolean showDate() {
        return showDate;
    }

    public boolean restartTasksOnUpdate() {
        return restartTasksOnUpdate;
    }

    public boolean multiLanguage() {
        return multiLanguage;
    }

    public int getAnnouncementsPerPage() {
        return announcementsPerPage;
    }

    public String getHoverMessage() {
        return hoverMessage;
    }

    public boolean isDebug() {
        return debug;
    }
}
