package nl.raspen0.serverannouncements;

import org.bukkit.entity.Player;

public class Announcement {

    private final String text;
    private final String permission;

    public Announcement(String text, String permission){
        this.text = text;
        this.permission = permission;
    }

    public String getText() {
        return text;
    }

    public boolean hasPermission(Player player){
        if(permission == null){
            return true;
        }
        return player.hasPermission(permission);
    }
}
