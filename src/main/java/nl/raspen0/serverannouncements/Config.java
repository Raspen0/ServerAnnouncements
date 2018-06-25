package nl.raspen0.serverannouncements;

import org.bukkit.configuration.file.FileConfiguration;

public class Config {

    private boolean restartTasksOnUpdate;
    private boolean showDate;
    private boolean multiLanguage;

    private int announcementsPerPage;

    Config(FileConfiguration config){
        this.restartTasksOnUpdate = config.getBoolean("restartTasksOnUpdate");
        this.showDate = config.getBoolean("announcements.showDate");
        this.multiLanguage = config.getBoolean("language.multiLanguage");
        this.announcementsPerPage = config.getInt("announcements.announcementsPerPage");
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
}
