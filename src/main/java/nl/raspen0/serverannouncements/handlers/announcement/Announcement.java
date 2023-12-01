package nl.raspen0.serverannouncements.handlers.announcement;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class Announcement {

    private String text;
    private final String permission;
    private final String title;
    private final LocalDate rawDate;

    Announcement(AnnouncementBuilder builder) {
        this.text = builder.text;
        this.permission = builder.permission;
        this.title = builder.title;
        this.rawDate = builder.date;
    }

    public Announcement(String text, String title, LocalDate date, String permission) {
        this.text = text;
        this.permission = permission;
        this.title = title;
        this.rawDate = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate(String locale) {
        String pattern = "dd-MMM";
        if(rawDate.getYear() != LocalDate.now().getYear()){
            pattern = "dd-MMM-yyyy";
        }
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern).withLocale(new Locale(locale));
        return rawDate.format(format);
    }

    public boolean hasPermission(Player player) {
        if (permission == null) {
            return true;
        }
        return player.hasPermission(permission);
    }

    static class AnnouncementBuilder {
        private String title;
        private MessageCreator.creatorState state;
        private String text;
        private String permission;
        private LocalDate date;

        AnnouncementBuilder(){
            this.state = MessageCreator.creatorState.TITLE;
            date = LocalDate.now();
        }

        void setPermission(String permission) {
            this.permission = permission;
        }

        void setTitle(String title) {
            this.title = title;
            state = MessageCreator.creatorState.TEXT;
        }

        String getText() {
            return text;
        }

        void setText(String text) {
            this.text = text;
            state = MessageCreator.creatorState.PERMISSION;
        }

        String getTitle() {
            return title;
        }

        MessageCreator.creatorState getState() {
            return state;
        }
    }
}