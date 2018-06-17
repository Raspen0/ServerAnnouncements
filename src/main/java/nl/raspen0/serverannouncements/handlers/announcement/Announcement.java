package nl.raspen0.serverannouncements.handlers.announcement;

import nl.raspen0.serverannouncements.handlers.announcement.MessageCreator;
import org.bukkit.entity.Player;

public class Announcement {

    private final String text;
    private final String permission;
    private final String title;

    Announcement(AnnouncementBuilder builder) {
        this.text = builder.text;
        this.permission = builder.permission;
        this.title = builder.title;
    }

    public Announcement(String title, String text, String permission) {
        this.text = text;
        this.permission = permission;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getPermission() {
        return permission;
    }

    public boolean hasPermission(Player player) {
        if (permission == null) {
            return true;
        }
        return player.hasPermission(permission);
    }

    public static class AnnouncementBuilder {

        private String title;
        private MessageCreator.creatorState state;
        private String text;
        private String permission;

        public AnnouncementBuilder(){
            this.state = MessageCreator.creatorState.TITLE;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public void setTitle(String title) {
            this.title = title;
            state = MessageCreator.creatorState.TEXT;
        }

        public void setText(String text) {
            this.text = text;
            state = MessageCreator.creatorState.PERMISSION;
        }

        public String getTitle() {
            return title;
        }

        public MessageCreator.creatorState getState() {
            return state;
        }

        public void setState(MessageCreator.creatorState state) {
            this.state = state;
        }
    }
}