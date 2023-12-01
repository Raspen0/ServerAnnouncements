package nl.raspen0.serverannouncements;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class Config {
    @Accessors(fluent = true)
    private final boolean restartTasksOnUpdate;
    @Accessors(fluent = true)
    private final boolean showDate;
    private final boolean multiLanguage;
    private final boolean debug;
    @Accessors(fluent = true)
    private final boolean useAdventureAPI;

    private final String hoverMessage;
    private final int announcementsPerPage;

    Config(FileConfiguration config){
        this.restartTasksOnUpdate = config.getBoolean("restartTasksOnUpdate");
        this.showDate = config.getBoolean("announcements.showDate");
        this.multiLanguage = config.getBoolean("language.multiLanguage");
        this.announcementsPerPage = config.getInt("announcements.announcementsPerPage");
        this.debug = config.getBoolean("debug.logging");
        this.hoverMessage = config.getString("announcements.hoverMessage");
        this.useAdventureAPI = config.getBoolean("useAdventureAPI");
    }
}
